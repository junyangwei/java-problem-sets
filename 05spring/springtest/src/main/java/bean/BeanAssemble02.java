package bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Java 中显示地装配 Bean
 *  - 使用 @Configuration 注解来声明这是一个配置类
 *      - 会为当前类默认调用构造函数注册一个 bean，名为 beanAssemble02
 *  - 在方法前使用 @Bean 注解来将返回的对象注册为 Spring 应用上下文的 bean
 * @author junyangwei
 * @date 2021-10-24
 */
@Data
@ToString
@Configuration
public class BeanAssemble02 {

    private int id;
    private String name;
    private List<String> list;

    public BeanAssemble02() {
        this.id = 3;
        this.name = "BeanAssembleTest02.";
        this.list = new ArrayList<String>();
        this.list.add("test02-01");
        this.list.add("test02-02");
    }

    /**
     * 主动注册一个名为 beanAssemble02-02 的 bean，调用 set 方法赋值
     */
    @Bean(name = "beanAssemble02-02")
    public BeanAssemble02 init() {
        List<String> listInit = new ArrayList<String>();
        listInit.add("test-02-03");
        listInit.add("test-02-04");

        BeanAssemble02 bean = new BeanAssemble02();
        bean.setId(4);
        bean.setName("BeanAssembleTest02..");
        bean.setList(listInit);
        return bean;
    }

    public static void main(String[] args) {
        // 加载 Spring 配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // [默认调用构造函数]获取 Spring 配置文件中注册的名为 "beanAssemble02" 的 bean
        BeanAssemble02 bean = (BeanAssemble02) context.getBean("beanAssemble02");

        System.out.println(bean);

        // [调用init方法]获取 Spring 配置文件中注册的名为 "beanAssemble02-02" 的 bean
        BeanAssemble02 bean02 = (BeanAssemble02) context.getBean("beanAssemble02-02");

        System.out.println(bean02);
    }
}
