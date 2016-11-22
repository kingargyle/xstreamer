package us.nineworlds.xstreamer.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;

import com.github.xws.XwsSpec;

import freemarker.template.Configuration;
import freemarker.template.Template;
import us.nineworlds.xstreamer.Activator;
import us.nineworlds.xstreamer.preferences.PreferenceConstants;

public class GenerateSquadJob extends Job {

	XwsSpec xwsspec;
	String playerFilename;
	String templateFilename;
	public GenerateSquadJob(String name, XwsSpec model, String preferncePlayerFileName, String templateFileName) {
		super(name);
		xwsspec = model;
		playerFilename = preferncePlayerFileName;
		this.templateFilename = templateFileName;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String templateOutputDirectory = preferenceStore.getString(PreferenceConstants.TEMPLATE_XSTREAMER_OUTPUT_DIRECTORY);
		String templateInputDirectory = preferenceStore.getString(PreferenceConstants.TEMPLATE_INPUT_DIRECTORY);
		if (StringUtils.isEmpty(templateOutputDirectory) || StringUtils.isEmpty(playerFilename) ||
			StringUtils.isEmpty(templateFilename) || StringUtils.isEmpty(templateInputDirectory)) {
			return Status.CANCEL_STATUS;
		}		

		Writer player1SquadFile = null;
		try {
			Configuration config = Activator.getFreemarkerConfig();
			config.setDirectoryForTemplateLoading(new File(templateInputDirectory));
			Path path = Paths.get(templateFilename);
			Template squadTemplate = config.getTemplate(path.getFileName().toString());
			player1SquadFile = new FileWriter(new File(templateOutputDirectory + File.separator + playerFilename));

			Map<String, Object> input = new HashMap<>();

			input.put("xwsspec", xwsspec);

			squadTemplate.process(input, player1SquadFile);
		} catch (Exception e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		} finally {
			IOUtils.closeQuietly(player1SquadFile);			
		}
		return Status.OK_STATUS;
	}
}
