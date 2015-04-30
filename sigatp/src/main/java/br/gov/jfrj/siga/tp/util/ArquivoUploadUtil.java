package br.gov.jfrj.siga.tp.util;

import java.io.IOException;
import java.io.InputStream;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

public class ArquivoUploadUtil {

	public static byte[] toByteArray(final UploadedFile upload) throws IOException {
		final InputStream is = upload.getFile();

		// Get the size of the file
		final long tamanho = upload.getSize();

		// N�o podemos criar um array usando o tipo long.
		// � necess�rio usar o tipo int.
		if (tamanho > Integer.MAX_VALUE)
			throw new IOException("Arquivo muito grande");

		// Create the byte array to hold the data
		final byte[] meuByteArray = new byte[(int) tamanho];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < meuByteArray.length && (numRead = is.read(meuByteArray, offset, meuByteArray.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < meuByteArray.length)
			throw new IOException("N�o foi poss�vel ler o arquivo completamente " + upload.getFileName());

		// Close the input stream and return bytes
		is.close();
		return meuByteArray;
	}
}
