package com.iastudio.vm

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Contrôle des applications : lit l'écran, touche, écrit, fait défiler. Le service ne fait RIEN tant que
 * l'IA n'est pas en mode « pilote » (tu démarres chaque tâche toi-même). Il doit être activé à la main dans
 * Réglages Android › Accessibilité › Studo.
 */
class StudoAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    /** Texte des éléments visibles avec leur position : « [bouton] Envoyer @540,1800 ». */
    fun dump(max: Int = 70): String {
        val root = rootInActiveWindow ?: return "(écran illisible : l'appli bloque la lecture ou l'écran est verrouillé)"
        val out = StringBuilder("Appli : ").append(root.packageName).append('\n')
        var n = 0
        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null || n >= max) return
            val t = node.text?.toString()?.trim().orEmpty()
            val d = node.contentDescription?.toString()?.trim().orEmpty()
            val label = t.ifEmpty { d }
            if (node.isVisibleToUser && (label.isNotEmpty() || node.isClickable || node.isEditable)) {
                val r = Rect()
                node.getBoundsInScreen(r)
                out.append(if (node.isEditable) "[champ] " else if (node.isClickable) "[bouton] " else "")
                    .append(label.take(60)).append(" @").append(r.centerX()).append(',').append(r.centerY()).append('\n')
                n++
            }
            for (i in 0 until node.childCount) walk(node.getChild(i))
        }
        walk(root)
        return out.toString().trimEnd()
    }

    private fun center(node: AccessibilityNodeInfo): Pair<Int, Int> {
        val r = Rect()
        node.getBoundsInScreen(r)
        return r.centerX() to r.centerY()
    }

    fun tapText(text: String): String {
        val root = rootInActiveWindow ?: throw RuntimeException("écran illisible")
        val found = root.findAccessibilityNodeInfosByText(text)
        val node = found.firstOrNull { it.isVisibleToUser } ?: throw RuntimeException("« $text » introuvable à l'écran")
        var cur: AccessibilityNodeInfo? = node
        while (cur != null) {
            if (cur.isClickable && cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return "touché « $text »"
            cur = cur.parent
        }
        val (x, y) = center(node)
        return tapXY(x, y)
    }

    fun tapXY(x: Int, y: Int): String {
        val p = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val g = GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p, 0, 70)).build()
        if (!dispatchGesture(g, null, null)) throw RuntimeException("geste refusé")
        return "touché ($x, $y)"
    }

    fun typeText(text: String): String {
        val root = rootInActiveWindow ?: throw RuntimeException("écran illisible")
        var target = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        if (target == null || !target.isEditable) {
            fun firstEditable(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
                if (n == null) return null
                if (n.isEditable && n.isVisibleToUser) return n
                for (i in 0 until n.childCount) firstEditable(n.getChild(i))?.let { return it }
                return null
            }
            target = firstEditable(root)
        }
        if (target == null) throw RuntimeException("aucun champ de texte trouvé")
        target.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        val args = Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text) }
        if (!target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)) throw RuntimeException("écriture refusée par ce champ")
        return "texte écrit (${text.length} caractères)"
    }

    fun scroll(direction: String): String {
        val dm = resources.displayMetrics
        val w = dm.widthPixels.toFloat()
        val h = dm.heightPixels.toFloat()
        val p = Path()
        when (direction) {
            "up" -> { p.moveTo(w / 2, h * 0.3f); p.lineTo(w / 2, h * 0.7f) }
            "left" -> { p.moveTo(w * 0.2f, h / 2); p.lineTo(w * 0.8f, h / 2) }
            "right" -> { p.moveTo(w * 0.8f, h / 2); p.lineTo(w * 0.2f, h / 2) }
            else -> { p.moveTo(w / 2, h * 0.7f); p.lineTo(w / 2, h * 0.3f) }
        }
        val g = GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p, 0, 350)).build()
        if (!dispatchGesture(g, null, null)) throw RuntimeException("geste refusé")
        return "défilement $direction"
    }

    fun press(key: String): String {
        val a = when (key) {
            "back" -> GLOBAL_ACTION_BACK
            "home" -> GLOBAL_ACTION_HOME
            "recents" -> GLOBAL_ACTION_RECENTS
            "notifications" -> GLOBAL_ACTION_NOTIFICATIONS
            "quick_settings" -> GLOBAL_ACTION_QUICK_SETTINGS
            else -> throw RuntimeException("touche inconnue : $key (back, home, recents, notifications, quick_settings)")
        }
        performGlobalAction(a)
        return "touche $key"
    }

    companion object {
        @Volatile
        var instance: StudoAccessibilityService? = null

        fun enabled() = instance != null
    }
}
