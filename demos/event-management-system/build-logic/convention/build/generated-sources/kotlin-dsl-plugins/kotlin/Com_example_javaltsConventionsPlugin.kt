/**
 * Precompiled [com.example.javalts-conventions.gradle.kts][Com_example_javalts_conventions_gradle] script plugin.
 *
 * @see Com_example_javalts_conventions_gradle
 */
public
class Com_example_javaltsConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Com_example_javalts_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
