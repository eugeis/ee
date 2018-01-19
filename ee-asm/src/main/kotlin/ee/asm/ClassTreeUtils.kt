package ee.asm

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

internal interface ClassTreeUtils {

    val classTree: ClassTree

    fun isExcluded(node: ClassNode): Boolean
    fun isExcluded(node: MethodNodeWithClass): Boolean

    fun findAvailableClasses(): List<ClassNode> = classTree.filter { !isExcluded(it) }

    fun findAvailableMethods(availableClasses: List<ClassNode>): List<MethodNodeWithClass> {
        return availableClasses.flatMap { classNode ->
            classNode.methods?.map { MethodNodeWithClass(classNode, it as MethodNode) }?.filter { !isExcluded(it) }
                    ?: listOf()
        }
    }

    val ClassNode.isView: Boolean
        get() {
            val isSuccessor = classTree.isSuccessorOf(this, "android/view/View") || this.name == "android/view/View"
            return isSuccessor && !isInner
        }

    val ClassNode.isLayoutParams: Boolean
        get() {
            return isInner && (classTree.isSuccessorOf(this,
                "android/view/ViewGroup\$LayoutParams") || this.name == "android/view/ViewGroup\$LayoutParams")
        }

    val ClassNode.isViewGroup: Boolean
        get() {
            return !isInner && (classTree.isSuccessorOf(this,
                "android/view/ViewGroup") || this.name == "android/view/ViewGroup")
        }

    fun ClassNode.resolveAllMethods(): List<MethodNode> {
        val node = classTree.findNode(this)

        fun allMethodsTo(node: ClassTreeNode?, list: MutableList<MethodNode>) {
            if (node == null) return
            list.addAll(node.data.methods as List<MethodNode>)
            allMethodsTo(node.parent, list)
        }

        val list = arrayListOf<MethodNode>()
        allMethodsTo(node, list)
        return list
    }

}