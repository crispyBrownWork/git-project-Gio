import java.io.File;

public class Git
{
    
    public static void main(String[] args) {
        init("\\\\wsl.localhost\\Ubuntu-20.04\\home\\HTCS\\git-project-Gio");
    }
    
    public static void init(String path){
        boolean exists = false;
        File initFile = new File(path);
        if(initFile.exists()){
            exists = true;
        }

        initFile.mkdir("git");
        File indexFile = new File("index");

        if(indexFile.exists()){
            exists = true;
        }

        if(exists){
            System.out.println("Git Repository already exists");
        } else {
            System.out.println("Created Repo: " + path);
        }
    }
}