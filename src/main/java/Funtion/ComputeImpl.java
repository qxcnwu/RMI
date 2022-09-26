package Funtion;

import Annotation.Visited;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 11:04
 * @Version 1.0
 * @PACKAGE Funtion
 */
@NoArgsConstructor
public class ComputeImpl implements Compute {
    @Override
    @Visited
    public int add(int @NotNull [] nums) {
        AtomicInteger ans = new AtomicInteger();
        Arrays.stream(nums).parallel().forEach(ans::addAndGet);
        return ans.get();
    }

    @Override
    @Visited
    public int mutiply(int @NotNull [] nums) {
        AtomicReference<Integer> ans = new AtomicReference<>(1);
        Arrays.stream(nums).forEach(i -> {
            ans.updateAndGet(v -> v * i);
        });
        return ans.get();
    }
}
