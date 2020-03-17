public class WorkerProcess
{
    public static void main(String[] args)
    {
        for (int i = 0; i < 10; i++) {
            System.out.println("Worker process woke up ("+(i+1)+"/10)");
            try {
                Thread.sleep(2000);
            } catch(InterruptedException e) {}
        }
        System.out.println("Worker process finished !");
    }
}
