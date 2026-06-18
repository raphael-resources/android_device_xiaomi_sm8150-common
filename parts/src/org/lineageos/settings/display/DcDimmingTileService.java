/*
* Copyright (C) 2018 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.lineageos.settings.display;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import vendor.lineage.livedisplay.IAntiFlicker;

public class DcDimmingTileService extends TileService {

    private static final String TAG = "DcDimmingTileService";

    private static IAntiFlicker getAntiFlickerService() {
        return IAntiFlicker.Stub.asInterface(
                ServiceManager.waitForService(
                        IAntiFlicker.DESCRIPTOR + "/default"));
    }

    private void updateUI(boolean enabled) {
        final Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        try {
            IAntiFlicker service = getAntiFlickerService();
            if (service != null) {
                updateUI(service.getEnabled());
            } else {
                Log.e(TAG, "Anti flicker service not available");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get dc dimming state", e);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        try {
            IAntiFlicker service = getAntiFlickerService();
            if (service == null) {
                Log.e(TAG, "Anti flicker service not available");
                return;
            }
            final boolean enabled = !service.getEnabled();
            service.setEnabled(enabled);
            updateUI(enabled);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to toggle dc dimming", e);
        }
    }
}
