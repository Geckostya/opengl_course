package nedikov.utils

import uno.buffer.toBuf
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO

fun readImage(filePath: String): BufferedImage {
    val url = ClassLoader.getSystemResource(filePath)
    val file = File(url.toURI())

    return ImageIO.read(file)
}

fun BufferedImage.toBuffer() = (raster.dataBuffer as DataBufferByte).data.toBuf()