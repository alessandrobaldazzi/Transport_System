package it.polito.wa2.group22.travelerservice.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO


class QRCodeGenerator {
    companion object {

        fun generate(text: String, width: Int , height: Int): String
        //throws WriterException, IOException
        {
            var qrCodeWriter = QRCodeWriter();
            var bitMatrix: BitMatrix  = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            var pngOutputStream = ByteArrayOutputStream();
            var con = MatrixToImageConfig(0xFF000002.toInt(), 0xFFFFC041.toInt());

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
            var pngData = pngOutputStream.toByteArray();
            val image = Base64.getEncoder().encodeToString(pngData)
            return image;
        }

        fun decodeQR(qrCodeBytes: ByteArray?): String? {
            try {
                val byteArrayInputStream = ByteArrayInputStream(qrCodeBytes)
                val bufferedImage = ImageIO.read(byteArrayInputStream)
                val bufferedImageLuminanceSource = BufferedImageLuminanceSource(bufferedImage)
                val hybridBinarizer = HybridBinarizer(bufferedImageLuminanceSource)
                val binaryBitmap = BinaryBitmap(hybridBinarizer)
                val multiFormatReader = MultiFormatReader()
                val result = multiFormatReader.decode(binaryBitmap)
                return result.getText()
            } catch (e: Exception) {
                return null
            }
            return null
        }
    }

}