import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Git implements GitInterface {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    }

    // initializes \\git, \\git\\objects, \\git\\index directories and files in the
    // working directory
    public static void init() throws IOException {
        File gitDir = new File("git");
        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");
        File headFile = new File("git" + File.separator + "HEAD");

        if (repoExists(gitDir, objectsDir, indexFile, headFile)) {
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
        if (!headFile.exists()) {
            headFile.createNewFile();
        }
    }

    private static boolean repoExists(File git, File objects, File index, File head) {
        return git.exists() && objects.exists() && index.exists() && head.exists();
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
        } else {
            blob.delete();
            return;
        }

        // Insert a new line entry into the index file
        try (FileWriter indexWriter = new FileWriter("git" + File.separator + "index", true)) {
            indexWriter.write(filename + " blob " + readFile.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createTree(String directoryPath) throws NoSuchAlgorithmException {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        StringBuilder treeContent = new StringBuilder();

        for (File file : files) {
            if (file.isDirectory()) {
                String dirHash = createTree(file.getPath());
                treeContent.append(dirHash).append(" tree ").append(file.getName()).append("\n");
            } else {
                String fileHash = hashedString(fileString(file));
                createBlob(file, false);
                treeContent.append(fileHash).append(" blob ").append(file.getName()).append("\n");
            }
        }


        String treeHash = hashedString(treeContent.toString());
        File treeFile = new File("git" + File.separator + "objects" + File.separator + treeHash);

        if(treeFile.exists()) {
            treeFile.delete();
            return treeHash;
        }
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

        return treeHash;
    }


    //create root tree without deleting 
    public static String createSnapshot(String workingDirectory) throws NoSuchAlgorithmException {
        return createTree(workingDirectory);
    }

    /*
     * create root tree
     * delete all duplicate files
     */
    public void stage(String workingDirectory) {
        try (FileWriter indexWriter = new FileWriter("git" + File.separator + "index", false)) {
            indexWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            createSnapshot(workingDirectory);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String commit(String author, String message) {
        String workingDirectory = "workingDirectory";
        File workingFile = new File(workingDirectory);
        File objectsDirectory = new File(workingDirectory + "/objects");
        File headFile = new File("git/HEAD");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String time = LocalDate.now().format(timeFormatter);
        String commitHash = "";

        StringBuilder commitContent = new StringBuilder();
        try {
            commitContent.append("tree: " + createSnapshot("workingDirectory") + "\n");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        commitContent.append("parent: " + fileString(headFile) +  "\n");
        commitContent.append("author: " + author + "\n");
        commitContent.append("date: " + time + "\n");
        commitContent.append("message: " + message);

        try {
            commitHash = hashedString(commitContent.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try (FileWriter headWriter = new FileWriter(headFile)) {
            headWriter.write(hashedString(commitContent.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        File commitFile = new File("git/objects/" + commitHash);
        try (FileWriter commitWriter = new FileWriter(commitFile)) {
            if (!commitFile.exists()) {
                commitFile.createNewFile();
            }
            commitWriter.write(commitContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commitHash;
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
