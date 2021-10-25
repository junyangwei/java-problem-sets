package autoassemble;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author junyangwei
 * @date 2021-10-24
 */
public class Starter {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        Student student123 = (Student) context.getBean("student123");
        System.out.println(student123);

        student123.print();

        Student student100 = (Student) context.getBean("student100");
        System.out.println(student100);

        student100.print();

        Klass class1 = (Klass) context.getBean("class1");
        System.out.println(class1);

        ISchool school = (School) context.getBean("school");
        System.out.println(school);

        school.ding();

        class1.dong();

        System.out.println("   context.getBeanDefinitionNames() ===>> "+ String.join(",", context.getBeanDefinitionNames()));

        Student s101 = (Student) context.getBean("s101");
        if (s101 != null) {
            System.out.println(s101);
        }
        Student s102 = (Student) context.getBean("s102");
        if (s102 != null) {
            System.out.println(s102);
        }
    }
}
