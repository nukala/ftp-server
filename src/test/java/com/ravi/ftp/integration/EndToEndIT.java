package com.ravi.ftp.integration;

import com.ravi.ftp.IntegrationTests;
import com.ravi.ftp.app.ClientApp;
import com.ravi.ftp.app.FtpServerApp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ravi.ftp.fs.DirListerTest.mkTmpDir;
import static com.ravi.ftp.fs.DirListerTest.mkTmpFile;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTests.class)
public class EndToEndIT {
    private static final boolean DBG = false;
    public static FtpServerApp app;
    private static Path tmpDir;

    @BeforeClass
    public static void beforeAnyTest() throws IOException {
        // Create a temporary folder and write 3 files in it:
        //   fn.txt - has about 11+2_new_lines
        //   aa.htm - should not be listed
        //   empty.txt
        tmpDir = mkTmpDir("e2e-");
        Path fn = mkTmpFile(tmpDir, "fn.txt");
        Files.write(fn, String.format("fntxt%nsecond%n").getBytes(UTF_8));
        Path aa = mkTmpFile(tmpDir, "aa.htm");
        Files.write(aa, String.format("<html/>").getBytes(UTF_8));
        mkTmpFile(tmpDir, "empty.txt");

        app = new FtpServerApp();
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> app.begin(new String[]{tmpDir.toString()})
                );
    }

    @AfterClass
    public static void afterAllTests() {
        //app.stop();
        // since ordering of tests in JUnit is a no-no
        //  "execute" a quit command.
        String pfx = "quit";
        ClientApp app = new ClientApp(new String[]{"quit"});
        app.doWork();
    }

    @Test
    public void getExistingEndToEnd() throws IOException {
        String pfx = "existing";
        ClientApp app = new ClientApp(new String[]{"get", "fn.txt"});
        app.doWork();
        BufferedReader rdr = new BufferedReader(new StringReader(app.getContent()));
        String status = rdr.readLine();
        String content = ClientApp.readEntire(rdr);
        if (DBG) {
            System.out.printf("%s: --- Client status=[%s] %n", pfx, status);
            System.out.printf("%s: --- Client content=[%s].%d %n", pfx, content, content.length());
        }

        assertThat(status)
                .as("verification of status")
                .isNotEmpty()
                .isEqualTo("ok");
        assertThat(content)
                .as("content check")
                .isNotEmpty()
                .contains("fntxt")
                .contains("second");
    }

    @Test
    public void unknownFileEndToEnd() throws IOException {
        String pfx = "missing";
        ClientApp app = new ClientApp(new String[]{"get", "unknonw.txt"});
        app.doWork();
        BufferedReader rdr = new BufferedReader(new StringReader(app.getContent()));
        String status = rdr.readLine();
        String content = ClientApp.readEntire(rdr);
        if (DBG) {
            System.out.printf("%s: --- Client status=[%s] %n", pfx, status);
            System.out.printf("%s: --- Client content=[%s].%d %n", pfx, app.getContent(), content.length());
        }

        assertThat(status)
                .as("status of missing file")
                .isEqualTo("error");
        assertThat(content)
                .as("content of missing file")
                .isEmpty();
    }

    @Test
    public void indexEndToEnd() {
        String pfx = "index";
        ClientApp app = new ClientApp(new String[]{"index"});
        app.doWork();
        if (DBG) {
            System.out.printf("%s: --- Client=[%s].%d %n", pfx, app.getContent(), app.getContent().length());
        }

        assertThat(app.getContent())
                .as("content of index")
                .isNotEmpty()
                .contains("fn.txt")
                .contains("empty.txt")
                .doesNotContain("aa.htm");
    }

    @Test
    public void disallowedEndToEnd() {
        String pfx = "disallowed";
        ClientApp app = new ClientApp(new String[]{"get", "aa.htm"});
        app.doWork();

        if (DBG) {
            System.out.printf("%s: --- Client=[%s].%d %n", pfx, app.getContent(), app.getContent().length());
        }
        assertThat(app.getContent())
                .isNotEmpty()
                .contains("error");
    }

    @Test
    public void unknownCommandEndToEnd() {
        String pfx = "unknown";
        ClientApp app = new ClientApp(new String[]{pfx});
        app.doWork();

        if (DBG) {
            System.out.printf("%s: --- Client=[%s].%d %n", pfx, app.getContent(), app.getContent().length());
        }
        assertThat(app.getContent())
                .isNotEmpty()
                .contains("unknown command");
    }

    @Test
    public void wierdPerformanceTest() throws InterruptedException {
        // wierd coz I used naps instead of countdown latch and such.
        //  Also there is no assert
        //  Further it is the same small file (15bytes?) over-and-over
        int numThreads = 100;
        int numCalls = 100 * numThreads;
        long napMillis = 10L;
        ExecutorService svc = Executors.newFixedThreadPool(numThreads);
        AtomicInteger count = new AtomicInteger();
        long timer = -System.currentTimeMillis();
        try {
            for (int i = 0; i < numCalls; i++) {
                svc.submit(() -> {
                    ClientApp app = new ClientApp(new String[]{"get fn.txt"});
                    app.doWork();

                    count.incrementAndGet();
                });
            }
            // inefficient way to wait, but this should do for now!
            while (count.get() < numCalls) {
                Thread.sleep(napMillis);
            }
        } finally {
            timer += System.currentTimeMillis();

            // assume the last nap was un-necessary, hence subtract. THIS IS NOT EXACT
            double perCall = (((timer - napMillis) * 1000.0) / numCalls) / 1000.0;
            System.out.printf("%d calls with %d threads, consumed %d ms (%2.2f millis/call) %n", numCalls, numThreads, timer, perCall);

            svc.shutdown();
            if (!svc.isShutdown()) {
                svc.shutdownNow();
            }
        }
    }
}
