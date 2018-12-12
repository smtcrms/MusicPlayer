package code.name.monkey.retromusic.ui.fragments.mainactivity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 13}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u0000 02\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u00012\u00020\u0004:\u00010B\u0005\u00a2\u0006\u0002\u0010\u0005J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u0002H\u0014J\b\u0010\u000f\u001a\u00020\u0003H\u0014J\b\u0010\u0010\u001a\u00020\u0007H\u0014J\b\u0010\u0011\u001a\u00020\u0007H\u0014J\b\u0010\u0012\u001a\u00020\u0013H\u0014J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\rH\u0016J\u0012\u0010\u0017\u001a\u00020\r2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\b\u0010\u001a\u001a\u00020\rH\u0016J\b\u0010\u001b\u001a\u00020\rH\u0016J\b\u0010\u001c\u001a\u00020\rH\u0016J\u0010\u0010\u001d\u001a\u00020\r2\u0006\u0010\u001e\u001a\u00020\u0007H\u0014J\u0010\u0010\u001f\u001a\u00020\r2\u0006\u0010\u001e\u001a\u00020\u0007H\u0014J\u0010\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\u0013H\u0014J\u0010\u0010\"\u001a\u00020\r2\u0006\u0010#\u001a\u00020\u0015H\u0014J\u0010\u0010$\u001a\u00020\r2\u0006\u0010%\u001a\u00020\u0007H\u0014J\u0010\u0010&\u001a\u00020\r2\u0006\u0010\'\u001a\u00020\u0015H\u0016J\u0010\u0010(\u001a\u00020\r2\u0006\u0010!\u001a\u00020\u0013H\u0014J\u0010\u0010)\u001a\u00020\r2\u0006\u0010#\u001a\u00020\u0015H\u0014J \u0010*\u001a\u00020\r2\u0016\u0010+\u001a\u0012\u0012\u0004\u0012\u00020-0,j\b\u0012\u0004\u0012\u00020-`.H\u0016J\b\u0010/\u001a\u00020\rH\u0016R\u0014\u0010\u0006\u001a\u00020\u00078TX\u0094\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcode/name/monkey/retromusic/ui/fragments/mainactivity/AlbumsFragment;", "Lcode/name/monkey/retromusic/ui/fragments/base/AbsLibraryPagerRecyclerViewCustomGridSizeFragment;", "Lcode/name/monkey/retromusic/ui/adapter/album/AlbumAdapter;", "Landroidx/recyclerview/widget/GridLayoutManager;", "Lcode/name/monkey/retromusic/mvp/contract/AlbumContract$AlbumView;", "()V", "emptyMessage", "", "getEmptyMessage", "()I", "presenter", "Lcode/name/monkey/retromusic/mvp/presenter/AlbumPresenter;", "completed", "", "createAdapter", "createLayoutManager", "loadGridSize", "loadGridSizeLand", "loadSortOrder", "", "loadUsePalette", "", "loading", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onMediaStoreChanged", "onResume", "saveGridSize", "gridColumns", "saveGridSizeLand", "saveSortOrder", "sortOrder", "saveUsePalette", "usePalette", "setGridSize", "gridSize", "setMenuVisibility", "menuVisible", "setSortOrder", "setUsePalette", "showData", "list", "Ljava/util/ArrayList;", "Lcode/name/monkey/retromusic/model/Album;", "Lkotlin/collections/ArrayList;", "showEmptyView", "Companion", "app_normalDebug"})
public class AlbumsFragment extends code.name.monkey.retromusic.ui.fragments.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment<code.name.monkey.retromusic.ui.adapter.album.AlbumAdapter, androidx.recyclerview.widget.GridLayoutManager> implements code.name.monkey.retromusic.mvp.contract.AlbumContract.AlbumView {
    private code.name.monkey.retromusic.mvp.presenter.AlbumPresenter presenter;
    private static final java.lang.String TAG = null;
    public static final code.name.monkey.retromusic.ui.fragments.mainactivity.AlbumsFragment.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    @java.lang.Override()
    protected int getEmptyMessage() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected androidx.recyclerview.widget.GridLayoutManager createLayoutManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected code.name.monkey.retromusic.ui.adapter.album.AlbumAdapter createAdapter() {
        return null;
    }
    
    @java.lang.Override()
    public boolean loadUsePalette() {
        return false;
    }
    
    @java.lang.Override()
    protected void setUsePalette(boolean usePalette) {
    }
    
    @java.lang.Override()
    protected void setGridSize(int gridSize) {
    }
    
    @java.lang.Override()
    protected void setSortOrder(@org.jetbrains.annotations.NotNull()
    java.lang.String sortOrder) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected java.lang.String loadSortOrder() {
        return null;
    }
    
    @java.lang.Override()
    protected void saveSortOrder(@org.jetbrains.annotations.NotNull()
    java.lang.String sortOrder) {
    }
    
    @java.lang.Override()
    protected int loadGridSize() {
        return 0;
    }
    
    @java.lang.Override()
    protected void saveGridSize(int gridColumns) {
    }
    
    @java.lang.Override()
    protected int loadGridSizeLand() {
        return 0;
    }
    
    @java.lang.Override()
    protected void saveGridSizeLand(int gridColumns) {
    }
    
    @java.lang.Override()
    protected void saveUsePalette(boolean usePalette) {
    }
    
    @java.lang.Override()
    public void onMediaStoreChanged() {
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void setMenuVisibility(boolean menuVisible) {
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public void loading() {
    }
    
    @java.lang.Override()
    public void showEmptyView() {
    }
    
    @java.lang.Override()
    public void completed() {
    }
    
    @java.lang.Override()
    public void showData(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<code.name.monkey.retromusic.model.Album> list) {
    }
    
    public AlbumsFragment() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 13}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\b\u001a\u00020\tR\u0019\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\n"}, d2 = {"Lcode/name/monkey/retromusic/ui/fragments/mainactivity/AlbumsFragment$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "getTAG", "()Ljava/lang/String;", "newInstance", "Lcode/name/monkey/retromusic/ui/fragments/mainactivity/AlbumsFragment;", "app_normalDebug"})
    public static final class Companion {
        
        public final java.lang.String getTAG() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final code.name.monkey.retromusic.ui.fragments.mainactivity.AlbumsFragment newInstance() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}