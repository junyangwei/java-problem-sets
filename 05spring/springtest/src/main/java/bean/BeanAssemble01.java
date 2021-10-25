package bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * XML 中显示地装配 Bean
 * @author junyangwei
 * @date 2021-10-24
 */
@Data
@ToString
public class BeanAssemble01 {

    private int id;
    private String name;
    private List<String> list;

    public BeanAssemble01() {
        this.id = 1;
        this.name = "BeanAssembleTest01.";
        this.list = new ArrayList<String>();
        this.list.add("test01-01");
        this.list.add("test01-02");
    }

    public static void main(String[] args) {
        // 加载 Spring 配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 获取 Spring 配置文件中注册的名为 "beanAssemble01-01" 的 bean
        BeanAssemble01 bean01 = (BeanAssemble01) context.getBean("beanAssemble01-01");

        System.out.println(bean01);

        // 获取 Spring 配置文件中注册的名为 "beanAssemble01-02" 的 bean
        BeanAssemble01 bean02 = (BeanAssemble01) context.getBean("beanAssemble01-02");

        System.out.println(bean02);
    }
}
