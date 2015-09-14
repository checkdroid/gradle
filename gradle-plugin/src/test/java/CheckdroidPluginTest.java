import com.checkdroid.gradle.cdRunTasks;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.testng.annotations.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Varad on 9/3/2015.
 */
public class CheckdroidPluginTest {
    @Test
    public void CheckdroidPluginAddscdRunTestsToProject(){
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("checkdroid");
        for(Task task : project.getTasksByName("cdRunTests", true)) {
            assertTrue(task instanceof cdRunTasks);
        }
    }
}
