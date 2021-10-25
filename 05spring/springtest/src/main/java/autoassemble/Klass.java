package autoassemble;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junyangwei
 * @date 2021-10-24
 */
@Data
@ToString
@Configuration
public class Klass {

    List<Student> students;

    @Resource(name = "student123")
    Student student123;

    @Resource(name = "student100")
    Student student100;

    @Bean(name = "class1")
    @DependsOn(value = { "student123", "student100"})
    public Klass init() {
        List<Student> list = new ArrayList<>();
        list.add(student123);
        list.add(student100);

        Klass class1 = new Klass();
        class1.setStudents(list);
        return class1;
    }

    public void dong() {
        System.out.println(this.getStudents());
    }
}
