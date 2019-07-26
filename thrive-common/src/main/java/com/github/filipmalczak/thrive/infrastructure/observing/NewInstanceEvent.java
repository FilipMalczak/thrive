package com.github.filipmalczak.thrive.infrastructure.observing;

import lombok.*;
import org.springframework.cloud.zookeeper.discovery.watcher.DependencyState;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class NewInstanceEvent {
    String name;
    DependencyState state;
}
