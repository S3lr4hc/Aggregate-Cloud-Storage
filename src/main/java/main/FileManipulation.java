package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * A class to take a file and split it into smaller files 
 * 
 * @author Keith Trnka, mshannon
 */
public class FileManipulation {
	
	public static void main(String[] args) throws IOException {
		/*ArrayList<Double> sizes = new ArrayList<Double>();
		sizes.add((double) (5 * 1024));
		sizes.add((double) (6 * 1024));
		sizes.add((double) (7 * 1024));
		
		splitFile("C:/Users/hp/Desktop/1.1-1.2.docx", sizes);*/
		//System.out.println(getNumberParts("C:/Users/hp/Desktop/STTREND_Reflection_Paper_No_6.docx"));
		//deleteAll("C:/Users/hp/Desktop/STTREND_Reflection_Paper_No_9.docx");
		//join("C:/Users/hp/Desktop/STTREND_Reflection_Paper_No_9.docx");
	}
	
	public void splitFile(String filename, ArrayList<Double> driveSizes) throws IOException {
		// open the file
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
				
		// get the file length
		File f = new File(filename);
		Double fileSize = (double) f.length();
				
		// loop for each full chunk
		int subfile;
		Double currentSize = fileSize;
		//assume all drive sizes are acquired
		//fileSize/chunkSize to be changed to value depending on drivesize
		//chunksize dependent of storage sizes
		//1st part will be placed on the largest storage
		for (subfile = 0; subfile < driveSizes.size() - 1/*fileSize / chunkSize[i]*/; subfile++)
		{
			if(currentSize < driveSizes.get(subfile))
				break;
			// open the output file
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename + "." + (subfile+1)));
					
			// write the right amount of bytes
			for (int currentByte = 0; currentByte < driveSizes.get(subfile); currentByte++)
			{
				// load one byte from the input file and write it to the output file
				out.write(in.read());
			}
			currentSize -= driveSizes.get(subfile);
			System.out.println("FileSize:"+fileSize+" ChunkSize:"+driveSizes.get(subfile));
			// close the file
			out.close();
		}
				
		// loop for the last chunk (which may be smaller than the chunk size)
		if (currentSize != 0)
		{
			// open the output file
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename + "." + (subfile+1)));
					
			// write the rest of the file
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
						
			// close the file
			out.close();			
		}
				
		// close the file
		in.close();
	}
	
	public void splitFile(String filename, long splitSize) throws IOException {
		
		int bufferSize = (int) (2 * splitSize);
		
		// String source = args[0];
		String source = filename;

		// String output = args[1];
		String output = filename;

		FileChannel sourceChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();

			ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

			long totalBytesRead = 0; // total bytes read from channel
			long totalBytesWritten = 0; // total bytes written to output

			double numberOfChunks = Math.ceil(sourceChannel.size() / (double) splitSize);
			int padSize = (int) Math.floor(Math.log10(numberOfChunks) + 1);
			String outputFileFormat = "%s.%0" + padSize + "d";

			FileChannel outputChannel = null; // output channel (split file) we are currently writing
			long outputChunkNumber = 0; // the split file / chunk number
			long outputChunkBytesWritten = 0; // number of bytes written to chunk so far

			try {
				for (int bytesRead = sourceChannel.read(buffer); bytesRead != -1; bytesRead = sourceChannel.read(buffer)) {
					totalBytesRead += bytesRead;

					System.out.println(String.format("Read %d bytes from channel; total bytes read %d/%d ", bytesRead,
							totalBytesRead, sourceChannel.size()));

					buffer.flip(); // convert the buffer from writing data to buffer from disk to reading mode

					int bytesWrittenFromBuffer = 0; // number of bytes written from buffer

					while (buffer.hasRemaining()) {
						if (outputChannel == null) {
							outputChunkNumber++;
							outputChunkBytesWritten = 0;
			
							String outputName = String.format(outputFileFormat, output, outputChunkNumber);
							System.out.println(String.format("Creating new output channel %s", outputName));
							outputChannel = new FileOutputStream(outputName).getChannel();
					    }

						long chunkBytesFree = (splitSize - outputChunkBytesWritten); // maximum free space in chunk
						int bytesToWrite = (int) Math.min(buffer.remaining(), chunkBytesFree); // maximum bytes that should be read from current byte buffer

						System.out.println(
								String.format(
										"Byte buffer has %d remaining bytes; chunk has %d bytes free; writing up to %d bytes to chunk",
										buffer.remaining(), chunkBytesFree, bytesToWrite));

						buffer.limit(bytesWrittenFromBuffer + bytesToWrite); // set limit in buffer up to where bytes can be read

						int bytesWritten = outputChannel.write(buffer);

						outputChunkBytesWritten += bytesWritten;
						bytesWrittenFromBuffer += bytesWritten;
						totalBytesWritten += bytesWritten;

						System.out.println(
								String.format(
										"Wrote %d to chunk; %d bytes written to chunk so far; %d bytes written from buffer so far; %d bytes written in total",
										bytesWritten, outputChunkBytesWritten, bytesWrittenFromBuffer, totalBytesWritten));

						buffer.limit(bytesRead); // reset limit

						if (totalBytesWritten == sourceChannel.size()) {
							System.out.println("Finished writing last chunk");

							closeChannel(outputChannel);
							outputChannel = null;

							break;
						}
						else if (outputChunkBytesWritten == splitSize) {
							System.out.println("Chunk at capacity; closing()");

							closeChannel(outputChannel);
							outputChannel = null;
						}
					}

					buffer.clear();
				}
			}
			finally {
				closeChannel(outputChannel);
			}
		}
		finally {
			closeChannel(sourceChannel);
		}
		
	}
	public void join(String baseFilename) throws IOException {
		
		int numberParts = getNumberParts(baseFilename);

		// now, assume that the files are correctly numbered in order (that some joker didn't delete any part)
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(baseFilename));
		for (int part = 1; part <= numberParts; part++) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(baseFilename + "." + part));

			int b;
			while ( (b = in.read()) != -1 )
				out.write(b);
				in.close();
		}
		out.close();
	}
	public void deleteAll(String baseFilename) throws IOException {
		int numberParts = getNumberParts(baseFilename);
		
		for(int part = 1; part <= numberParts; part++) {
			File file = new File(baseFilename + "." + part);
			
			if(file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println(baseFilename + "." + part);
				System.out.println("Delete operation is failed.");
			}
		}
	}
	public void deleteFile(String baseFilename) throws IOException {
		File file = new File(baseFilename);
		
		if(file.delete()) {
			System.out.println(file.getName() + " is deleted!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}
	public int getNumberParts(String baseFilename) throws IOException {
		// list all files in the same directory
		File directory = new File(baseFilename).getAbsoluteFile().getParentFile();
		final String justFilename = new File(baseFilename).getName();
		String[] matchingFiles = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(justFilename) && name.substring(justFilename.length()).matches("^\\.\\d+$");
			}
		});
		return matchingFiles.length;
	}
	private static void closeChannel(FileChannel channel) {
		if (channel != null) {
			try {
				channel.close();
			} catch (Exception ignore) {
				;
			}
		}
	}
}
