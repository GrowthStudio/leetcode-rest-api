
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Created by Charvis on 27/05/2017.
 */

fun createProject() {
    val serviceAccount = FileInputStream("leetcode-firebase-adminsdk.json")

    val options = FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
            .setDatabaseUrl("https://leetcode-d1d73.firebaseio.com/")
            .build()

    FirebaseApp.initializeApp(options)

    val database = FirebaseDatabase.getInstance().getReference()
    database.child("algorithms").addChildEventListener(object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildAdded(ds: DataSnapshot?, p1: String?) {
            val algorithm = ds!!.getValue(Algorithm::class.java)
            try {
                createKotlinFile(algorithm)
                createJavaFile(algorithm)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }

        override fun onChildRemoved(p0: DataSnapshot?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })
}
val directory = "/Users/Charvis/leetcode/src/main"

fun createKotlinFile(algorithm: Algorithm) {
    val fileName = String.format("%03d-%s", algorithm.num, algorithm.id)
    val functionName = normalizeFunctionName(algorithm.id)
    val className = normalizeClassName(algorithm.id)

    val ktPath = Paths.get(directory, "kotlin", fileName + ".kt")
    if(Files.notExists(ktPath)) {
        Files.createFile(ktPath)
    }

    val mdPath = Paths.get(directory, "kotlin", fileName + ".md")
    if(Files.notExists(mdPath)) {
        Files.createFile(mdPath)
    }

    val kotlin = """
@file:JvmName("${className}Kt")

import org.junit.*

/**
 * Solution
 * ---
${markdownToKDoc(algorithm.question_markdown, " * ")}
 */
fun ${functionName}() {

}

/**
 * Tests
 */
class ${className}Test() {
    @Test fun test1() {

    }

    @Test fun test2() {

    }
}
"""
    Files.write(ktPath, kotlin.lines())

    val markdown = """
# Problem

${algorithm.question_markdown}

[${algorithm.title}](${algorithm.link})

# Solution


"""
    Files.write(mdPath, markdown.lines())
}

fun createJavaFile(algorithm: Algorithm) {
    val packageName = String.format("_%03d_%s", algorithm.num, algorithm.id.replace("-", "_"))
    val packagePath = Paths.get(directory, "java", packageName)
    if (Files.notExists(packagePath)) {
        Files.createDirectory(packagePath)
    }

    val javaPath = Paths.get(directory, "java", packageName, "Solution.java")
    if (Files.notExists(javaPath)) {
        Files.createFile(javaPath)
    }

    val mdPath = Paths.get(directory, "java", packageName, "README.md")
    if (Files.notExists(mdPath)) {
        Files.createFile(mdPath)
    }

    val testPath = Paths.get(directory, "java", packageName, "Tests.java")
    if (Files.notExists(testPath)) {
        Files.createFile(testPath)
    }

    val methodName = normalizeFunctionName(algorithm.id)
    val java = """
package ${packageName};

/**
 * ${algorithm.link}
 */
public class Solution {
    public void ${methodName}() {

    }
}
"""
    Files.write(javaPath, java.lines())

    val test = """
package ${packageName};
import org.junit.*;

public class Tests {
	private Solution solution = new Solution();

	@Test public void test1() {

	}

    @Test public void test2() {

	}
}
"""
    Files.write(testPath, test.lines())

    val markdown = """
# Problem

${algorithm.question_markdown}

[${algorithm.title}](${algorithm.link})

# Solution


"""
    Files.write(mdPath, markdown.lines())

}
fun normalizeFunctionName(str: String): String {
    val func = str.split("-").map { it.capitalize() }.joinToString(separator = "").decapitalize()
    return if(func[0].isDigit()) "_${func}" else func
}

fun normalizeClassName(str: String): String {
    val func = str.split("-").map { it.capitalize() }.joinToString(separator = "")
    return if(func[0].isDigit()) "_${func}" else func
}

fun markdownToKDoc(str: String, prefix: String): String {
    return str.lines().map { "${prefix}${it}" }.joinToString(separator = "\n")
}

fun main(args: Array<String>) {
    createProject()

    readLine()
}

data class Algorithm(
    val id: String = "",
    val num: Int = -1,
    val title: String = "",
    val question: String = "",
    var question_markdown: String = "",
    val difficulty: String = "",
    val acceptance: String = "",
    val link: String = "",
    val tags: List<String> = listOf()
)