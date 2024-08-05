package pekaeds.settings;

import java.io.*;

public class LevelTestingSettings {
    public boolean devMode = false;
        
    public boolean customWorkingDirectory = false;
    
    public String workingDirectory = "/home/saturnin/c++/pk2_greta";

    public boolean customExecutable = false;
    public String executable = "./bin/pekka-kana-2";

    public void load(DataInputStream in) throws IOException{
        this.devMode = in.readBoolean();

        this.customWorkingDirectory = in.readBoolean();
        this.workingDirectory = in.readUTF();
        
        this.customExecutable = in.readBoolean();
        this.executable = in.readUTF();
    }

    public void save(DataOutputStream out) throws IOException{
        out.writeBoolean(this.devMode);

        out.writeBoolean(this.customWorkingDirectory);

        out.writeUTF(this.workingDirectory);

        out.writeBoolean(this.customExecutable);

        out.writeUTF(this.executable);
    }
}
