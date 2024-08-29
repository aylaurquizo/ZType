import java.util.Random;

public class Main {
  public static void main(String[] args) {
    ZTypeWorld world = new ZTypeWorld(new MtLoWord(), new Random(), 0, 0);
    world.bigBang(400, 600, .05);
  }
}
