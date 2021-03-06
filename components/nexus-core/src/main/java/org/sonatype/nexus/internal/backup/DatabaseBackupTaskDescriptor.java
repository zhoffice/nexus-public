/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.internal.backup;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.i18n.I18N;
import org.sonatype.goodies.i18n.MessageBundle;
import org.sonatype.nexus.common.node.NodeAccess;
import org.sonatype.nexus.formfields.ComboboxFormField;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.scheduling.TaskConfiguration;
import org.sonatype.nexus.scheduling.TaskDescriptorSupport;

import static org.sonatype.nexus.formfields.FormField.MANDATORY;

/**
 * {@link DatabaseBackupTask} descriptor.
 *
 * @since 3.2
 */
@Named
@Singleton
public class DatabaseBackupTaskDescriptor
    extends TaskDescriptorSupport
{
  public static final String TYPE_ID = "db.backup";

  public static final String BACKUP_LOCATION = "location";

  public static final String BACKUP_NODE_ID = "nodeId";

  private interface Messages
      extends MessageBundle
  {
    @DefaultMessage("Database backup")
    String name();

    @DefaultMessage("Backup location")
    String locationLabel();

    @DefaultMessage("Filesystem location for backup data")
    String locationHelpText();

    @DefaultMessage("Node performing backup")
    String backupNodeLabel();

    @DefaultMessage("Where should backups be performed")
    String backupNodeHelpText();

  }

  private static final Messages messages = I18N.create(Messages.class);

  @Inject
  public DatabaseBackupTaskDescriptor(final NodeAccess nodeAccess)
  {
    super(TYPE_ID, DatabaseBackupTask.class, messages.name(), VISIBLE, EXPOSED,
        new StringTextFormField(
            BACKUP_LOCATION,
            messages.locationLabel(),
            messages.locationHelpText(),
            MANDATORY
        ),
        nodeFormField(nodeAccess));
  }

  @Override
  public void initializeConfiguration(final TaskConfiguration configuration) {
    configuration.setBoolean(MULTINODE_KEY, true);
  }

  private static FormField<String> nodeFormField(final NodeAccess nodeAccess) {
    FormField<String> nodeField;
    if (nodeAccess.isClustered()) {
      nodeField = new ComboboxFormField<String>(
          BACKUP_NODE_ID,
          messages.backupNodeLabel(),
          messages.backupNodeHelpText(),
          MANDATORY
      ).withStoreApi("node_NodeAccess.nodes").withIdMapping("name");
    }
    else {
      nodeField = null;
    }
    return nodeField;
  }

}
