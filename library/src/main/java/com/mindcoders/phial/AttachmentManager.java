package com.mindcoders.phial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
class AttachmentManager {
    private final List<AttachmentProvider> providers;

    AttachmentManager(List<AttachmentProvider> providers) {
        this.providers = providers;
    }

    List<File> prepareAttachments() throws Exception {
        final List<File> result = new ArrayList<>(providers.size());
        for (AttachmentProvider provider : providers) {
            result.add(provider.provideAttachment());
        }
        return result;
    }
}
