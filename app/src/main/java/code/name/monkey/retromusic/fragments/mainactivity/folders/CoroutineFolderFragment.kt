/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.fragments.mainactivity.folders

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.adapter.SongFileAdapter
import code.name.monkey.retromusic.fragments.base.AbsMainActivityFragment
import code.name.monkey.retromusic.interfaces.CabHolder
import code.name.monkey.retromusic.interfaces.MainActivityFragmentCallbacks
import code.name.monkey.retromusic.util.FileUtil
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.ViewUtil
import code.name.monkey.retromusic.views.BreadCrumbLayout
import com.afollestad.materialcab.MaterialCab
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext

class CoroutineFolderFragment : MainActivityFragmentCallbacks, CoroutineScope, AbsMainActivityFragment(), BreadCrumbLayout.SelectionCallback, SongFileAdapter.Callbacks, CabHolder, AppBarLayout.OnOffsetChangedListener {

    private lateinit var cab: MaterialCab
    private val job = Job()
    private lateinit var songFileAdapter: SongFileAdapter


    override val coroutineContext: CoroutineContext = job + Main
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            setCrumb(BreadCrumbLayout.Crumb(FileUtil.safeGetCanonicalFile(arguments?.getSerializable(PATH) as File?)), true)
        } else {
            breadCrumbs.restoreFromStateWrapper(savedInstanceState.getParcelable(CRUMBS))
            loadData()
        }
    }

    private fun loadData() {
        launch {
            val folder = getActiveCrumb()?.file
            if (folder != null) {
                val files = FileUtil.listFiles(folder, audioFilerFilter)
                files.sort()
                updateAdapter(files)
            } else {
                LinkedList<File>()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusbarColorAuto(view)

        //Bread Crumbs initialize
        breadCrumbs.setCallback(this)

        setupToolbar()
        setupRecyclerView()
        setupAdapter()

    }

    override fun onResume() {
        super.onResume()
        appBarLayout.addOnOffsetChangedListener(this)
    }

    override fun onPause() {
        job.cancel()
        super.onPause()
        appBarLayout.removeOnOffsetChangedListener(this)
    }

    private fun setupToolbar() {
        val primaryColor = ThemeStore.primaryColor(mainActivity)

        appBarLayout.setBackgroundColor(primaryColor)
        toolbar.setBackgroundColor(primaryColor)

        mainActivity.title = null
        mainActivity.setSupportActionBar(toolbar)

        breadCrumbs.setActivatedContentColor(ToolbarContentTintHelper.toolbarTitleColor(activity!!, ColorUtil.darkenColor(primaryColor)))
        breadCrumbs.setDeactivatedContentColor(ToolbarContentTintHelper.toolbarSubtitleColor(activity!!,
                ColorUtil.darkenColor(primaryColor)))
    }

    //RecyclerView initialize
    private fun setupRecyclerView() {
        recyclerView.apply {
            ViewUtil.setUpFastScrollRecyclerViewColor(mainActivity, recyclerView, ThemeStore.accentColor(mainActivity))
            layoutManager = LinearLayoutManager(mainActivity)
        }

    }

    //Initialize SongFileAdapter
    private fun setupAdapter() {
        songFileAdapter = SongFileAdapter(mainActivity, LinkedList(), R.layout.item_list, this, this)
        songFileAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
        recyclerView.adapter = songFileAdapter
        checkIsEmpty()
    }

    private fun checkIsEmpty() {
        if (empty != null) {
            empty.visibility = if (songFileAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    private fun updateAdapter(files: List<File>) {
        songFileAdapter.swapDataSet(files)
        val crumb = getActiveCrumb()
        if (crumb != null && recyclerView != null) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(crumb.scrollPosition, 0)
        }
    }

    private fun setCrumb(crumb: BreadCrumbLayout.Crumb?, addToHistory: Boolean) {
        if (crumb == null) {
            return
        }
        saveScrollPosition()
        breadCrumbs.setActiveOrAdd(crumb, false)
        if (addToHistory) {
            breadCrumbs.addHistory(crumb)
        }
        loadData()
    }

    private fun saveScrollPosition() {
        val crumb = getActiveCrumb()
        if (crumb != null) {
            crumb.scrollPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
    }

    private fun getActiveCrumb(): BreadCrumbLayout.Crumb? {
        return if (breadCrumbs != null && breadCrumbs.size() > 0) {
            breadCrumbs.getCrumb(breadCrumbs.activeIndex)
        } else {
            null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (breadCrumbs != null) {
            outState.putParcelable(CRUMBS, breadCrumbs.stateWrapper)
        }
    }

    override fun handleBackPress(): Boolean {
        if (cab != null && cab.isActive) {
            cab.finish()
            return true
        }
        if (breadCrumbs != null && breadCrumbs.popHistory()) {
            setCrumb(breadCrumbs.lastHistory(), false)
            return true
        }
        return false
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mainActivity.setLightStatusbar(!ATHUtil.isWindowBackgroundDark(context!!))
        container.setPadding(container.paddingLeft, container.paddingTop,
                container.paddingRight, this.appBarLayout.totalScrollRange + verticalOffset)
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb?, index: Int) {

    }

    override fun onFileSelected(tempFile: File) {
        val file = tryGetCanonicalFile(tempFile)
        if (file != null && file.isDirectory) {
            setCrumb(BreadCrumbLayout.Crumb(file), true)
        } else {
            val fileFiler = FileFilter { pathname -> !pathname.isDirectory && audioFilerFilter.accept(pathname) }

        }
    }

    override fun onFileMenuClicked(file: File, view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMultipleItemAction(item: MenuItem, files: ArrayList<File>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val PATH = "path"
        private const val CRUMBS = "crumbs"

        private val audioFilerFilter: FileFilter = FileFilter { file ->
            !file.isHidden && (file.isDirectory or
                    FileUtil.fileIsMimeType(file, "audio/*", MimeTypeMap.getSingleton()) or
                    FileUtil.fileIsMimeType(file, "application/opus", MimeTypeMap.getSingleton()) or
                    FileUtil.fileIsMimeType(file, "application/ogg", MimeTypeMap.getSingleton()))
        }

        fun newInstance(context: Context): CoroutineFolderFragment {
            return newInstance(PreferenceUtil.getInstance().startDirectory)
        }

        fun newInstance(file: File): CoroutineFolderFragment {
            val coroutineFolderFragment = CoroutineFolderFragment()
            val bundle = Bundle()
            bundle.putSerializable(PATH, file)
            coroutineFolderFragment.arguments = bundle
            return coroutineFolderFragment

        }

        fun getDefaultStartDirectory(): File {
            val musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            return if (musicDirectory.exists() && musicDirectory.isDirectory) {
                musicDirectory
            } else {
                val externalStorage = Environment.getExternalStorageDirectory()
                if (externalStorage.exists() && externalStorage.isDirectory) {
                    externalStorage
                } else {
                    File("/") //Root
                }
            }
        }

        private fun tryGetCanonicalFile(file: File): File? {
            return try {
                file.canonicalFile
            } catch (err: IOException) {
                err.printStackTrace()
                file
            }
        }
    }
}