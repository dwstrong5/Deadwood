public class DeadwoodMain {

    public static void main(String[] args) {
        DeadwoodView view = new DeadwoodView();
        DeadwoodController controller = new DeadwoodController(args, view);

        view.register(controller);

        view.show();
    }
}
