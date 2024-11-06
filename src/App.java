
import com.spendWise.*;

public class App {

    public static void main(String[] args) throws Exception {
        if (System.getProperty("java.version").compareTo("17.0") < 0) {
            System.err.println("Please update your java version to 17.0 or above to run this application.");
            System.exit(-1);
        }
        SpalshScreen.main(args);

    }
}
