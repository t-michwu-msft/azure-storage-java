/**
 * Copyright Microsoft Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.windowsazure.storage;

/**
 * Specifies the location mode used to decide which location the request should be sent to.
 */
public enum LocationMode {
    /**
     * Requests should always be sent to the primary location.
     */
    PRIMARY_ONLY,

    /**
     * Requests should always be sent to the primary location first. If the request fails, it should be sent to the
     * secondary location.
     */
    PRIMARY_THEN_SECONDARY,

    /**
     * Requests should always be sent to the secondary location.
     */
    SECONDARY_ONLY,

    /**
     * Requests should always be sent to the secondary location first. If the request fails, it should be sent to the
     * primary location.
     */
    SECONDARY_THEN_PRIMARY;
}
