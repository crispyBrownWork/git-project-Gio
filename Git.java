import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import java.math.BigInteger;

public class Git {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    }

    // initializes \\git, \\git\\objects, \\git\\index directories and files in the
    // working directory
    public static void init() throws IOException {
        File gitDir = new File("git");
        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");

        if (repoExists(gitDir, objectsDir, indexFile)) {
            System.out.println("Git Repository already exists");
            return;
        }

        if (!gitDir.exists()) {
            gitDir.mkdir();
        }
        if (!objectsDir.exists()) {
            objectsDir.mkdir();
        }
        if (!indexFile.exists()) {
            indexFile.createNewFile();
        }
    }

    private static boolean repoExists(File git, File objects, File index) {
        return git.exists() && objects.exists() && index.exists();
    }

    // Creates a blob from a file and records its hash in the index
    public static void createBlob(File readFile, boolean doCompress) throws NoSuchAlgorithmException {
        String filedata = fileString(readFile);
        String filename = hashedString(filedata);

        File blob = new File("git" + File.separator + "objects" + File.separator + filename);
        if (!blob.exists()) {
            try {
                blob.createNewFile();
                FileWriter objectsWriter = new FileWriter(blob);
                if (doCompress) {
                    objectsWriter.write(compressedHash(filedata));
                } else {
                    objectsWriter.write(filedata);
                }
                objectsWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Insert a new line entry into the index file
        try (FileWriter indexWriter = new FileWriter("git" + File.separator + "index", true)) {
            indexWriter.write(filename + " blob " + readFile.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTree(String directoryPath) throws NoSuchAlgorithmException {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        StringBuilder treeContent = new StringBuilder();

        for (File file : files) {
            if (file.isDirectory()) {
                createTree(file.getPath());
                String dirHash = hashedString(file.getName());
                treeContent.append(dirHash).append(" tree ").append(file.getName()).append("\n");
            } else {
                createBlob(file, false);
                String fileHash = hashedString(fileString(file));
                treeContent.append(fileHash).append(" blob ").append(file.getName()).append("\n");
            }
        }

        String treeHash = hashedString(treeContent.toString());
        File treeFile = new File("git" + File.separator + "objects" + File.separator + treeHash);
        try (FileWriter writer = new FileWriter(treeFile)) {
            writer.write(treeContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter indexWriter = new FileWriter("git" + File.separator + "index", true)) {
            indexWriter.write(treeHash + " tree " + directory.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads data off a file into a string
    public static String fileString(File readFile) {
        StringBuilder filedata = new StringBuilder();
        try (FileReader reader = new FileReader(readFile)) {
            int data;
            while ((data = reader.read()) != -1) {
                filedata.append((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filedata.toString();
    }

    // Hashes a string as SHA-1
    public static String hashedString(String filedata) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] digest = md.digest(filedata.getBytes());
        BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.toString(16);
    }

    // Compresses a string using GZIP
    private static String compressedHash(String inputString) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zip = new GZIPOutputStream(out);
        zip.write(inputString.getBytes());
        zip.close();
        return out.toString();
    }
}
