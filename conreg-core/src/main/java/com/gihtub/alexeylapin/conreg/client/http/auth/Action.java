package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Action {

    public static final Action PULL = new Action("pull");
    public static final Action PUSH = new Action("push");
    public static final Action DELETE = new Action("delete");

    private final String name;

    public static Action of(String name) {
        return new Action(name);
    }

}
