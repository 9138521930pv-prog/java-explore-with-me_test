package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_subscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @EmbeddedId
    private UserSubscriptionId id;

    @MapsId("userSubscriberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_subscriber_id")
    private User subscriber;

    @MapsId("userTargetId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_target_id")
    private User targetUser;

}
