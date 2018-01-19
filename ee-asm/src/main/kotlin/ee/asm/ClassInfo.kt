package ee.asm

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InnerClassNode
import org.objectweb.asm.tree.MethodNode

data class MethodNodeWithClass(var clazz: ClassNode, val method: MethodNode) {
    val identifier = "${clazz.fqName}#${method.name}"
}

val ClassNode.fqName: String
    get() = name.replace('/', '.').replace('$', '.')

val ClassNode.packageName: String
    get() = fqName.substringBeforeLast('.')

val ClassNode.fqNameWithTypeArguments: String
    get() = fqName + buildTypeParams()

val ClassNode.simpleName: String
    get() {
        val name = fqName
        return if (name.indexOf('$') >= 0) name.substringAfterLast('$') else name.substringAfterLast('.')
    }

fun ClassNode.buildTypeParams(): String {
    return if (signature != null) {
        val genericMethodSignature = parseGenericMethodSignature(signature)
        if (genericMethodSignature.typeParameters.isEmpty()) return ""

        genericMethodSignature.typeParameters.map {
            it.upperBounds.fold("") { s, bound ->
                s + "out " + genericTypeToStr(bound)
            }
        }.joinToString(prefix = "<", postfix = ">")
    } else ""
}

val ClassNode.isInner: Boolean
    get() = name.contains("$")

val ClassNode.isAbstract: Boolean
    get() = ((access and Opcodes.ACC_ABSTRACT) != 0)

val ClassNode.isPublic: Boolean
    get() = ((access and Opcodes.ACC_PUBLIC) != 0)

val InnerClassNode.isPublic: Boolean
    get() = ((access and Opcodes.ACC_PUBLIC) != 0)

val InnerClassNode.isProtected: Boolean
    get() = ((access and Opcodes.ACC_PROTECTED) != 0)

val InnerClassNode.isInterface: Boolean
    get() = ((access and Opcodes.ACC_INTERFACE) != 0)

fun ClassNode.getConstructors(): List<MethodNode> {
    return (methods as List<MethodNode>).filter { it.isConstructor }
}