package nedikov.utils

import assimp.Importer
import glm_.f
import glm_.vec2.Vec2d
import glm_.vec3.Vec3
import imgui.IO
import imgui.ImGui
import imgui.impl.LwjglGL3
import nedikov.program.Mesh
import uno.glfw.glfw
import uno.kotlin.uri

// do not change window cursor
fun LwjglGL3.newFrameWithCursor() {

    // Setup display size (every frame to accommodate for window resizing)
    IO.displaySize put window.size
    IO.displayFramebufferScale.x = if (window.size.x > 0) window.framebufferSize.x / window.size.x.f else 0f
    IO.displayFramebufferScale.y = if (window.size.y > 0) window.framebufferSize.y / window.size.y.f else 0f

    // Setup time step
    val currentTime = glfw.time
    IO.deltaTime = if (time > 0) (currentTime - time).f else 1f / 60f
    time = currentTime

    /*  Setup inputs
        (we already got mouse wheel, keyboard keys & characters from glfw callbacks polled in glfwPollEvents())
        Mouse position in screen coordinates (set to -1,-1 if no mouse / on another screen, etc.)   */
    if (window.focused)
        if (IO.wantMoveMouse)
        /*  Set mouse position if requested by io.WantMoveMouse flag (used when io.NavMovesTrue is enabled by user
            and using directional navigation)   */
            window.cursorPos = Vec2d(IO.mousePos)
        else
        // Get mouse position in screen coordinates (set to -1,-1 if no mouse / on another screen, etc.)
            IO.mousePos put window.cursorPos
    else
        IO.mousePos put -Float.MAX_VALUE

    repeat(3) {
        /*  If a mouse press event came, always pass it as "mouse held this frame", so we don't miss click-release
            events that are shorter than 1 frame.   */
        IO.mouseDown[it] = mouseJustPressed[it] || window.mouseButton(it) != 0
        mouseJustPressed[it] = false
    }

    IO.mouseWheel = mouseWheel
    mouseWheel = 0f

    /*  Start the frame. This call will update the IO.wantCaptureMouse, IO.wantCaptureKeyboard flag that you can use
        to dispatch inputs (or not) to your application.         */
    ImGui.newFrame()
}

fun loadModel(file: String, defaultColor: Vec3 = Vec3(1f)): List<Mesh> {
    val meshes = mutableListOf<Mesh>()
    val scene = Importer().readFile(file.uri) ?: return meshes
    scene.meshes.forEach { mesh ->
        val vertices = FloatArray(8 * mesh.numVertices)
        val indices = IntArray(3 * mesh.numFaces)
        mesh.vertices.forEachIndexed { i, v ->
            val n = mesh.normals[i]
            v.to(vertices, i * 8)
            n.to(vertices, i * 8 + 3)
            if (mesh.textureCoords[0].isNotEmpty()) {
                val tc = mesh.textureCoords[0][i]
                vertices[i * 8 + 6] = tc[0]
                vertices[i * 8 + 7] = tc[1]
            }
        }
        repeat(indices.size) { indices[it] = mesh.faces[it / 3][it % 3] }

        val color = scene.materials[mesh.materialIndex].color?.diffuse ?: defaultColor
        meshes.add(Mesh(vertices, indices, color))
    }
    return meshes
}