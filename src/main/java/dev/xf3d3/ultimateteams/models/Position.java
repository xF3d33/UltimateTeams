/*
 * This file is part of HuskTowns, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a position in a claim world
 */
@SuppressWarnings("unused")
public class Position {

    @Getter @Setter
    @Expose
    private double x;

    @Getter @Setter
    @Expose
    private double y;

    @Getter @Setter
    @Expose
    private double z;

    @Getter @Setter
    @Expose
    private String world;

    @Getter @Setter
    @Expose
    private float yaw;

    @Getter @Setter
    @Expose
    private float pitch;

    protected Position(double x, double y, double z, @NotNull String world, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @SuppressWarnings("unused")
    private Position() {
    }

    @NotNull
    public static Position at(double x, double y, double z, @NotNull String world, float yaw, float pitch) {
        return new Position(x, y, z, world, yaw, pitch);
    }

    @NotNull
    public static Position at(double x, double y, double z, @NotNull String world) {
        return new Position(x, y, z, world, 0, 0);
    }
}