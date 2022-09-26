package pojo;

import Annotation.Structs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 19:54
 * @Version 1.0
 * @PACKAGE pojo
 */
@Structs
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetAddr implements Serializable {
    private String ip;
    private Integer port;
}
