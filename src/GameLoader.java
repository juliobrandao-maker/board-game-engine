import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
public class GameLoader {
    public static GameConfig LoadConfig(String caminhodoarquivo)throws IOException{
        Gson gson = new Gson();
        try (FileReader leitor = new FileReader(caminhodoarquivo)){
            return gson.fromJson(leitor,GameConfig.class);
        }
    }

}

