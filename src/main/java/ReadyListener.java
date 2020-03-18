public class ReadyListener implements EventListener
{
    public static void main(String[] args)
            throws LoginException, InterruptedException
    {
        // Note: It is important to register your ReadyListener before building
        JDA jda = new JDABuilder(System.getenv("TOKEN"))
            .addEventListeners(new ReadyListener())
            .build();

        // optionally block until JDA is ready
        jda.awaitReady();
    }

    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }
}
