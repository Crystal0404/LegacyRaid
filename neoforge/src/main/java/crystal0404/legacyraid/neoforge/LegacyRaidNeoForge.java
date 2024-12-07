/*
 * This file is part of the LegacyRaid project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Crystal0404 and contributors
 *
 * LegacyRaid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * LegacyRaid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LegacyRaid.  If not, see <https://www.gnu.org/licenses/>.
 */

package crystal0404.legacyraid.neoforge;

import crystal0404.legacyraid.LegacyRaid;
import net.neoforged.fml.common.Mod;

@Mod(LegacyRaid.MOD_ID)
public class LegacyRaidNeoForge {
    public LegacyRaidNeoForge() {
        LegacyRaid.init();
    }
}
