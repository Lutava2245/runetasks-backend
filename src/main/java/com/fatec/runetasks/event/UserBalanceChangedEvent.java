package com.fatec.runetasks.event;

import com.fatec.runetasks.domain.model.User;

public record UserBalanceChangedEvent(User user) {
}
