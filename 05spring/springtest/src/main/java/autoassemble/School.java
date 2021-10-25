package autoassemble;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author junyangwei
 * @date 2021-10-24
 */
@ToString
@Component
public class School implements ISchool {
    @Autowired(required = true)
    Klass class1;

    @Resource(name = "student100")
    Student student100;

    @Override
    public void ding() {
        System.out.println("Class1 have " + this.class1.getStudents().size());
    }
}
