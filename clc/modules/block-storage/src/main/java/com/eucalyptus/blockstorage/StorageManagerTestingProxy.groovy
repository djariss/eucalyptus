/*************************************************************************
 * Copyright 2009-2014 Ent. Services Development Corporation LP
 *
 * Redistribution and use of this software in source and binary forms,
 * with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************/
package com.eucalyptus.blockstorage

import com.eucalyptus.storage.common.CheckerTask
import com.eucalyptus.util.EucalyptusCloudException
import com.google.common.base.Function;
import com.google.common.collect.Maps
import edu.ucsb.eucalyptus.msgs.ComponentProperty
import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentMap

/**
 * A delegating wrapper to allow testing using arbitrary faked device capacities to test
 * failure behavior by the higher layers on async creation failures
 * Created by zhill on 6/20/14.
 */
@CompileStatic
class StorageManagerTestingProxy implements LogicalStorageManager {
  /*
   Would like to use @Delegate here, but doesn't allow method overrides...
   */
  LogicalStorageManager delegateStorageManager

  public StorageManagerTestingProxy(LogicalStorageManager manager) {
    this.delegateStorageManager = manager
  }

  protected static int fakeDeviceCapacity = -1
  protected static volatile int capacityUsed = 0
  protected static final ConcurrentMap<String, Integer> sizeMap = Maps.<String,Integer>newConcurrentMap()

  protected static void check(String name, int size) throws EucalyptusCloudException {
    if(fakeDeviceCapacity > 0) {
      addVolumeUsage(name, size)
      if(capacityUsed > fakeDeviceCapacity) {
        throw new EucalyptusCloudException("FakedInsufficientCapacity")
      }
    }
  }

  protected static void addVolumeUsage(String name, int size) {
    capacityUsed+=size
    sizeMap.put(name, size)
  }

  protected static void removeVolumeUsage(String name) {
    capacityUsed -= sizeMap.get(name) == null ? 0 : sizeMap.get(name)
  }

  public int createVolume(String volume, String snapshot, int size) throws EucalyptusCloudException {
    try {
      check(volume, size)
      return delegateStorageManager.createVolume(volume, snapshot, size)
    } catch(Throwable f) {
      removeVolumeUsage(volume)
      throw f
    }
  }

  @Override
  void cloneVolume(String volumeId, String parentVolumeId) throws EucalyptusCloudException {
    delegateStorageManager.cloneVolume(volumeId, parentVolumeId)
  }

  @Override
  void addSnapshot(String snapshotId) throws EucalyptusCloudException {
    delegateStorageManager.addSnapshot(snapshotId)
  }

  public void deleteVolume(String volume) throws EucalyptusCloudException {
    delegateStorageManager.deleteVolume(volume)
    removeVolumeUsage(volume)
  }

  @Override
  void deleteSnapshot(String snapshotId, String snapshotPointId) throws EucalyptusCloudException {
    removeVolumeUsage(snapshotId)
    delegateStorageManager.deleteSnapshot(snapshotId, snapshotPointId)
  }

  @Override
  String getVolumeConnectionString(String volumeId) throws EucalyptusCloudException {
    return delegateStorageManager.getVolumeConnectionString(volumeId)
  }

  @Override
  void loadSnapshots(List<String> snapshotSet, List<String> snapshotFileNames) throws EucalyptusCloudException {
    delegateStorageManager.loadSnapshots(snapshotSet, snapshotFileNames)
  }

  @Override
  int getSnapshotSize(String snapshotId) throws EucalyptusCloudException {
    return delegateStorageManager.getSnapshotSize(snapshotId)
  }

  @Override
  void finishVolume(String snapshotId) throws EucalyptusCloudException {
    delegateStorageManager.finishVolume(snapshotId)
  }

  @Override
  StorageResourceWithCallback prepSnapshotForDownload(String snapshotId, int sizeExpected, long actualSizeInMB) throws EucalyptusCloudException {
    return delegateStorageManager.prepSnapshotForDownload(snapshotId, sizeExpected, actualSizeInMB)
  }

  @Override
  ArrayList<ComponentProperty> getStorageProps() {
    return delegateStorageManager.getStorageProps()
  }

  @Override
  void setStorageProps(ArrayList<ComponentProperty> storageParams) {
    delegateStorageManager.setStorageProps(storageParams)
  }

  @Override
  String getStorageRootDirectory() {
    return delegateStorageManager.getStorageRootDirectory()
  }

  @Override
  String getVolumePath(String volumeId) throws EucalyptusCloudException {
    return delegateStorageManager.getVolumePath(volumeId)
  }

  @Override
  void importVolume(String volumeId, String volumePath, int size) throws EucalyptusCloudException {
    delegateStorageManager.importVolume(volumeId, volumePath, size)
  }

  @Override
  String getSnapshotPath(String snapshotId) throws EucalyptusCloudException {
    return delegateStorageManager.getSnapshotPath(snapshotId)
  }

  @Override
  void importSnapshot(String snapshotId, String snapPath, String volumeId, int size) throws EucalyptusCloudException {
    delegateStorageManager.importSnapshot(snapshotId, snapPath, volumeId, size)
  }

  @Override
  String exportVolume(String volumeId, String nodeIqn) throws EucalyptusCloudException {
    return delegateStorageManager.exportVolume(volumeId, nodeIqn)
  }

  @Override
  void unexportVolume(String volumeId, String nodeIqn) throws EucalyptusCloudException, UnsupportedOperationException {
    delegateStorageManager.unexportVolume(volumeId, nodeIqn)
  }

  @Override
  void unexportVolumeFromAll(String volumeId) throws EucalyptusCloudException {
    delegateStorageManager.unexportVolumeFromAll(volumeId)
  }

  @Override
  String createSnapshotPoint(String parentVolumeId, String volumeId) throws EucalyptusCloudException {
    return delegateStorageManager.createSnapshotPoint(parentVolumeId, volumeId)
  }

  @Override
  void deleteSnapshotPoint(String parentVolumeId, String volumeId, String snapshotPointId) throws EucalyptusCloudException {
    delegateStorageManager.deleteSnapshotPoint(parentVolumeId, volumeId, snapshotPointId)
  }

  @Override
  void checkReady() throws EucalyptusCloudException {
    delegateStorageManager.checkReady()
  }

  @Override
  void stop() throws EucalyptusCloudException {
    delegateStorageManager.stop()
  }

  @Override
  void enable() throws EucalyptusCloudException {
    delegateStorageManager.enable()
  }

  @Override
  void disable() throws EucalyptusCloudException {
    delegateStorageManager.disable()
  }

  @Override
  boolean getFromBackend(String snapshotId, int size) throws EucalyptusCloudException {
    return delegateStorageManager.getFromBackend(snapshotId, size)
  }

  @Override
  void checkVolume(String volumeId) throws EucalyptusCloudException {
    delegateStorageManager.checkVolume(volumeId)
  }

  @Override
  List<CheckerTask> getCheckers() {
    return delegateStorageManager.getCheckers()
  }

  public void createVolume(String volume, int size) throws EucalyptusCloudException {
    try {
      check(volume, size);
      delegateStorageManager.createVolume(volume, size)
    } catch(Throwable f) {
      removeVolumeUsage(volume)
      throw f
    }
  }

  @Override
  void initialize() throws EucalyptusCloudException {
    fakeDeviceCapacity = -1
    capacityUsed = 0
    sizeMap.clear()
    delegateStorageManager.initialize()
  }

  @Override
  void configure() throws EucalyptusCloudException {
    delegateStorageManager.configure()
  }

  @Override
  void checkPreconditions() throws EucalyptusCloudException {
    delegateStorageManager.checkPreconditions()
  }

  @Override
  void reload() {
    delegateStorageManager.reload()
  }

  @Override
  void startupChecks() throws EucalyptusCloudException {
    delegateStorageManager.startupChecks()
  }

  @Override
  void cleanVolume(String volumeId) {
    removeVolumeUsage(volumeId)
    delegateStorageManager.cleanVolume(volumeId)
  }

  @Override
  void cleanSnapshot(String snapshotId, String snapshotPointId) {
    removeVolumeUsage(snapshotId)
    delegateStorageManager.cleanSnapshot(snapshotId, snapshotPointId)
  }

  @Override
  void createSnapshot(String volumeId, String snapshotId, String snapshotPointId) throws EucalyptusCloudException {
    try {
      check(snapshotId, sizeMap.get(volumeId));
      delegateStorageManager.createSnapshot(volumeId, snapshotId, snapshotPointId)
    } catch(Throwable f) {
      removeVolumeUsage(volumeId)
      throw f
    }
  }

  @Override
  List<String> prepareForTransfer(String snapshotId) throws EucalyptusCloudException {
    return delegateStorageManager.prepareForTransfer(snapshotId)
  }

  @Override
  public boolean supportsIncrementalSnapshots() throws EucalyptusCloudException {
    return delegateStorageManager.supportsIncrementalSnapshots()
  }

  @Override
  public StorageResourceWithCallback prepIncrementalSnapshotForUpload(String volumeId, String snapshotId, String snapPointId, String prevSnapshotId,
      String prevSnapPointId) throws EucalyptusCloudException {
    try {
      check(snapshotId, sizeMap.get(volumeId))
      return delegateStorageManager.prepIncrementalSnapshotForUpload(volumeId, snapshotId, snapPointId, prevSnapshotId, prevSnapPointId)
    } catch(Throwable f) {
      removeVolumeUsage(volumeId)
      throw f
    }
  }

  @Override
  public StorageResource prepSnapshotForUpload(String volumeId, String snapshotId, String snapPointId) throws EucalyptusCloudException {
    try {
      check(snapshotId, sizeMap.get(volumeId));
      return delegateStorageManager.prepSnapshotForUpload(volumeId, snapshotId, snapPointId)
    } catch(Throwable f) {
      removeVolumeUsage(volumeId)
      throw f
    }
  }

  @Override
  public StorageResourceWithCallback prepSnapshotBaseForRestore(String snapshotId, int size, String snapshotPointId)
  throws EucalyptusCloudException {
    return delegateStorageManager.prepSnapshotBaseForRestore(snapshotId, size, snapshotPointId)
  }

  @Override
  public <F, T> T executeCallback(Function<F, T> callback, F input) throws EucalyptusCloudException {
    return delegateStorageManager.executeCallback(callback, input)
  }

  @Override
  public void restoreSnapshotDelta(String currentSnapId, String prevSnapId, String baseId, StorageResource sr) throws EucalyptusCloudException {
    delegateStorageManager.restoreSnapshotDelta(currentSnapId, prevSnapId, baseId, sr)
  }

  @Override
  public void completeSnapshotRestorationFromDeltas(String snapshotId) throws EucalyptusCloudException {
    delegateStorageManager.completeSnapshotRestorationFromDeltas(snapshotId)
  }
}
