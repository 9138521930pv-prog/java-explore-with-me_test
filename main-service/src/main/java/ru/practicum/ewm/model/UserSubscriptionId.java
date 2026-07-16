package ru.practicum.ewm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
public class UserSubscriptionId implements Serializable {

    @Column(name = "user_subscriber_id")
    private Long userSubscriberId;

    @Column(name = "user_target_id")
    private Long userTargetId;
}
