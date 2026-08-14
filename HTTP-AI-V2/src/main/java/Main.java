import experiment.ExperimentRunner;

public class Main {

    public static void main(String[] args)
            throws Exception {

        ExperimentRunner runner =
                new ExperimentRunner();

         String url =
                "https://www.yunlong-performance-research.com/index.html";

         int times = 1000;

         runner.runAll(
                url,
                times,
                "small"
         );

    }
}