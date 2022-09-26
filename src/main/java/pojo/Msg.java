package pojo;

import Annotation.Structs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @Author qxc
 * @Date 2022 2022/9/24 22:43
 * @Version 1.0
 * @PACKAGE pojo
 */
@Structs
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Msg {
    private NetAddr client;
}
