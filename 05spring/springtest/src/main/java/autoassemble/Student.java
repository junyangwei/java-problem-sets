package autoassemble;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author junyangwei
 * @date 2021-10-24
 */
@Data
@ToString
@Configuration
public class Student implements Serializable, BeanNameAware, ApplicationContextAware {

    private int id;
    private String name;

    private String beanName;
    private ApplicationContext applicationContext;

    public Student() {
        this.id = 101;
        this.name = "KK101";
        this.beanName = null;
        this.applicationContext = null;
    }

    @Bean(name = "student123")
    public Student init01() {
        System.out.println("#### Begin init student123 ####");
        Student student123 = new Student();
        student123.setId(123);
        student123.setName("KK123");
        return student123;
    }

    @Bean(name = "student100")
    public Student init02() {
        System.out.println("#### Begin init student100 ####");
        Student student123 = new Student();
        student123.setId(100);
        student123.setName("KK100");
        return student123;
    }

    public static Student create() {
        return new Student();
    }

    public void print() {
        System.out.println(this.beanName);
        System.out.println("   context.getBeanDefinitionNames() ===>> "
                + String.join(",", applicationContext.getBeanDefinitionNames()));

    }
}

