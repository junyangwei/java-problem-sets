package bean;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 隐式地 Bean 扫描发现机制 和 自动装配
 * @author junyangwei
 * @date 2021-10-24
 */
@ToString
@Component
public class BeanAssemble03 {

    private int id;
    private String name;
    private List<String> list;

    @Autowired
    public BeanAssemble03() {
        this.id = 5;
        this.name = "BeanAssembleTest03.";
        this.list = new ArrayList<String>();
        this.list.add("test03-01");
        this.list.add("test03-02");
    }

    public static void main(String[] args) {
        // 加载 Spring 配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // [默认调用构造函数]获取 Spring 配置文件中注册的名为 "beanAssemble03" 的 bean
        BeanAssemble03 bean = (BeanAssemble03) context.getBean("beanAssemble03");

        System.out.println(bean);

    }

}
