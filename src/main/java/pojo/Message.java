package pojo;

import Annotation.Structs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.util.HashMap;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 19:40
 * @Version 1.0
 * @PACKAGE pojo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Structs
public class Message implements Serializable {
    private String interfaceName;
    private String methodHash;
    private byte[] args;
    private HashMap<String,Object> map;
    private Object ans;
    private String msg;
    private NetAddr client;
    private NetAddr service;

    /**
     * 解码字节流
     */
    @SneakyThrows
    public void decodeMap(){
        ByteArrayOutputStream byt=new ByteArrayOutputStream();
        ObjectOutputStream obj=new ObjectOutputStream(byt);
        obj.writeObject(map);
        this.args=byt.toByteArray();
    }

    /**
     * 编码字节流
     */
    @SneakyThrows
    public void encodeMap(){
        ByteArrayInputStream byteInt=new ByteArrayInputStream(args);
        ObjectInputStream objInt=new ObjectInputStream(byteInt);
        map= (HashMap<String, Object>) objInt.readObject();
    }
}
