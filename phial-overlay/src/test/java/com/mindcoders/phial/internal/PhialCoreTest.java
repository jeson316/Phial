package com.mindcoders.phial.internal;

import com.mindcoders.phial.ListAttacher;
import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.keyvalue.InfoWriter;
import com.mindcoders.phial.internal.share.UserShareItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rost on 11/28/17.
 */
@RunWith(RobolectricTestRunner.class)
public class PhialCoreTest {

    @Test
    public void create_with_default_values() {
        final PhialBuilder builder = spy(new PhialBuilder(RuntimeEnvironment.application));
        final List<InfoWriter> writers = Stream.generate(() -> mock(InfoWriter.class)).limit(2).collect(Collectors.toList());
        when(builder.getInfoWriters()).thenReturn(writers);

        final PhialCore phialCore = PhialCore.create(builder);

        assertNotNull(phialCore.getApplication());
        assertNotNull(phialCore.getKvSaver());
        assertNotNull(phialCore.getScreenTracker());
        assertNotNull(phialCore.getNotifier());
        assertNotNull(phialCore.getShareManager());
        assertTrue(phialCore.getShareManager().getShareables().isEmpty());
        assertNotNull(phialCore.getAttachmentManager());
        assertNotNull(phialCore.getActivityProvider());
        assertEquals(2, phialCore.getAttachmentManager().getProviders().size());
        assertNotNull(phialCore.getSharedPreferences());
        final List<Page> pages = phialCore.getPages();
        assertEquals(2, pages.size());
        verifyPage(pages.get(0), PhialCore.KEYVALUE_PAGE_KEY);
        verifyPage(pages.get(1), PhialCore.SHARE_PAGE_KEY);

        verify(writers.get(0)).writeInfo();
        verify(writers.get(1)).writeInfo();
    }

    @Test
    public void create_without_pages() {
        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);
        builder.enableKeyValueView(false);
        builder.enableShareView(false);

        final PhialCore phialCore = PhialCore.create(builder);
        assertTrue(phialCore.getPages().isEmpty());
    }

    @Test
    public void create_with_user_pages() {
        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);
        final List<Page> pages = generatePages(5);

        builder.addPages(pages);
        builder.enableKeyValueView(false);
        builder.enableShareView(false);

        final PhialCore phialCore = PhialCore.create(builder);
        assertEquals(pages, phialCore.getPages());
    }

    @Test
    public void create_with_user_and_default_pages() {
        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);
        final List<Page> pages = generatePages(5);

        builder.addPages(pages);
        builder.enableKeyValueView(true);
        builder.enableShareView(true);

        final PhialCore phialCore = PhialCore.create(builder);
        final List<Page> createdPages = phialCore.getPages();

        assertEquals(createdPages.size(), pages.size() + 2);
        assertEquals(PhialCore.KEYVALUE_PAGE_KEY, createdPages.get(0).getId());
        assertEquals(PhialCore.SHARE_PAGE_KEY, createdPages.get(1).getId());
        assertTrue(createdPages.containsAll(pages));
    }

    @Test
    public void create_with_custom_sharables() {
        final List<Shareable> shareables = Stream.generate(() -> mock(Shareable.class)).limit(2)
                .collect(Collectors.toList());

        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);
        shareables.forEach(builder::addShareable);

        final PhialCore phialCore = PhialCore.create(builder);
        final List<Shareable> createdShareables = phialCore.getShareManager()
                .getUserShareItems()
                .stream()
                .map(item -> (UserShareItem) item)
                .map(UserShareItem::getShareable)
                .collect(Collectors.toList());

        assertEquals(shareables, createdShareables);
    }

    @Test
    public void create_with_custom_attachers() {
        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);
        builder.attachKeyValues(false).attachScreenshot(false);

        final List<ListAttacher> attachers = Stream.generate(() -> mock(ListAttacher.class)).limit(2)
                .collect(Collectors.toList());
        attachers.forEach(builder::addAttachmentProvider);

        final PhialCore phialCore = PhialCore.create(builder);
        assertEquals(attachers, phialCore.getAttachmentManager().getProviders());
    }

    @Test
    public void create_with_custom_and_default_attachers() {
        final PhialBuilder builder = new PhialBuilder(RuntimeEnvironment.application);

        final List<ListAttacher> attachers = Stream.generate(() -> mock(ListAttacher.class)).limit(2)
                .collect(Collectors.toList());
        attachers.forEach(builder::addAttachmentProvider);

        final PhialCore phialCore = PhialCore.create(builder);
        assertTrue(phialCore.getAttachmentManager().getProviders().containsAll(attachers));
    }

    private void verifyPage(Page page, String expectedKey) {
        assertEquals(expectedKey, page.getId());
        assertNotNull(page.getPageViewFactory());
        assertNotNull(page.getPageViewFactory().createPageView(
                RuntimeEnvironment.application,
                mock(OverlayCallback.class))
        );
    }

    private List<Page> generatePages(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::createPage)
                .collect(Collectors.toList());
    }

    private Page createPage(int i) {
        return new Page(String.valueOf(i), i, "title" + i, null);
    }
}
