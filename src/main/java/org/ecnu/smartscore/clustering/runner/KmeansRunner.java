package org.ecnu.smartscore.clustering.runner;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.ClassUtils;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.ecnu.smartscore.clustering.conversion.InputDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KmeansRunner extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(KmeansRunner.class);
	private static final String DIRECTORY_CONTAINING_CONVERTED_INPUT = "data";

	private KmeansRunner() {
	}

	public static void run(String inputPath, String outputPath)
			throws Exception {
		LOGGER.info(
				"Running with default arguments, input/output path: {}, {}",
				inputPath, outputPath);

		Path input = new Path(inputPath);
		Path output = new Path(outputPath);
		Configuration conf = new Configuration();
		try {
			LOGGER.debug("Cleanup...");
			HadoopUtil.delete(conf, output);

			LOGGER.debug("Running...");
			_run(conf, input, output, new EuclideanDistanceMeasure(), 6, 0.5,
					10);
		} catch (Exception e) {
			LOGGER.error("Run task Kmeans error!", e);
			throw e;
		}
	}

	@Override
	@Deprecated
	public int run(String[] args) throws Exception {
		addInputOption();
		addOutputOption();
		addOption(DefaultOptionCreator.distanceMeasureOption().create());
		addOption(DefaultOptionCreator.numClustersOption().create());
		addOption(DefaultOptionCreator.t1Option().create());
		addOption(DefaultOptionCreator.t2Option().create());
		addOption(DefaultOptionCreator.convergenceOption().create());
		addOption(DefaultOptionCreator.maxIterationsOption().create());
		addOption(DefaultOptionCreator.overwriteOption().create());

		Map<String, List<String>> argMap = parseArguments(args);
		if (argMap == null) {
			return -1;
		}

		Path input = getInputPath();
		Path output = getOutputPath();
		String measureClass = getOption(DefaultOptionCreator.DISTANCE_MEASURE_OPTION);
		if (measureClass == null) {
			measureClass = SquaredEuclideanDistanceMeasure.class.getName();
		}
		double convergenceDelta = Double
				.parseDouble(getOption(DefaultOptionCreator.CONVERGENCE_DELTA_OPTION));
		int maxIterations = Integer
				.parseInt(getOption(DefaultOptionCreator.MAX_ITERATIONS_OPTION));
		if (hasOption(DefaultOptionCreator.OVERWRITE_OPTION)) {
			HadoopUtil.delete(getConf(), output);
		}
		DistanceMeasure measure = ClassUtils.instantiateAs(measureClass,
				DistanceMeasure.class);
		if (hasOption(DefaultOptionCreator.NUM_CLUSTERS_OPTION)) {
			int k = Integer
					.parseInt(getOption(DefaultOptionCreator.NUM_CLUSTERS_OPTION));
			_run(getConf(), input, output, measure, k, convergenceDelta,
					maxIterations);
		} else {
			double t1 = Double
					.parseDouble(getOption(DefaultOptionCreator.T1_OPTION));
			double t2 = Double
					.parseDouble(getOption(DefaultOptionCreator.T2_OPTION));
			run(getConf(), input, output, measure, t1, t2, convergenceDelta,
					maxIterations);
		}
		return 0;
	}

	/**
	 * Run the kmeans clustering job on an input dataset using the given the
	 * number of clusters k and iteration parameters. All output data will be
	 * written to the output directory, which will be initially deleted if it
	 * exists. The clustered points will reside in the path
	 * <output>/clustered-points. By default, the job expects a file containing
	 * equal length space delimited data that resides in a directory named
	 * "testdata", and writes output to a directory named "output".
	 * 
	 * @param conf
	 *            the Configuration to use
	 * @param input
	 *            the String denoting the input directory path
	 * @param output
	 *            the String denoting the output directory path
	 * @param measure
	 *            the DistanceMeasure to use
	 * @param k
	 *            the number of clusters in Kmeans
	 * @param convergenceDelta
	 *            the double convergence criteria for iterations
	 * @param maxIterations
	 *            the int maximum number of iterations
	 */
	private static void _run(Configuration conf, Path input, Path output,
			DistanceMeasure measure, int k, double convergenceDelta,
			int maxIterations) throws Exception {
		Path directoryContainingConvertedInput = new Path(output,
				DIRECTORY_CONTAINING_CONVERTED_INPUT);
		LOGGER.info("Preparing Input");
		InputDriver.runJob(input, directoryContainingConvertedInput,
				"org.apache.mahout.math.RandomAccessSparseVector");
		LOGGER.info("Running random seed to get initial clusters");
		Path clusters = new Path(output, "random-seeds");
		clusters = RandomSeedGenerator.buildRandom(conf,
				directoryContainingConvertedInput, clusters, k, measure);
		LOGGER.info("Running KMeans with k = {}", k);
		KMeansDriver.run(conf, directoryContainingConvertedInput, clusters,
				output, convergenceDelta, maxIterations, true, 0.0, false);
		// run ClusterDumper
		// Path outGlob = new Path(output, "clusters-*-final");
		// Path clusteredPoints = new Path(output, "clusteredPoints");
		// log.info(
		// "Dumping out clusters from clusters: {} and clusteredPoints: {}",
		// outGlob, clusteredPoints);
		// ClusterDumper clusterDumper = new ClusterDumper(outGlob,
		// clusteredPoints);
		// clusterDumper.printClusters(null);
	}

	/**
	 * Run the kmeans clustering job on an input dataset using the given
	 * distance measure, t1, t2 and iteration parameters. All output data will
	 * be written to the output directory, which will be initially deleted if it
	 * exists. The clustered points will reside in the path
	 * <output>/clustered-points. By default, the job expects the a file
	 * containing synthetic_control.data as obtained from
	 * http://archive.ics.uci.
	 * edu/ml/datasets/Synthetic+Control+Chart+Time+Series resides in a
	 * directory named "testdata", and writes output to a directory named
	 * "output".
	 * 
	 * @param conf
	 *            the Configuration to use
	 * @param input
	 *            the String denoting the input directory path
	 * @param output
	 *            the String denoting the output directory path
	 * @param measure
	 *            the DistanceMeasure to use
	 * @param t1
	 *            the canopy T1 threshold
	 * @param t2
	 *            the canopy T2 threshold
	 * @param convergenceDelta
	 *            the double convergence criteria for iterations
	 * @param maxIterations
	 *            the int maximum number of iterations
	 */
	@Deprecated
	public static void run(Configuration conf, Path input, Path output,
			DistanceMeasure measure, double t1, double t2,
			double convergenceDelta, int maxIterations) throws Exception {
		Path directoryContainingConvertedInput = new Path(output,
				DIRECTORY_CONTAINING_CONVERTED_INPUT);
		LOGGER.info("Preparing Input");
		InputDriver.runJob(input, directoryContainingConvertedInput,
				"org.apache.mahout.math.RandomAccessSparseVector");
		LOGGER.info("Running Canopy to get initial clusters");
		Path canopyOutput = new Path(output, "canopies");
		CanopyDriver.run(new Configuration(),
				directoryContainingConvertedInput, canopyOutput, measure, t1,
				t2, false, 0.0, false);
		LOGGER.info("Running KMeans");
		KMeansDriver.run(conf, directoryContainingConvertedInput, new Path(
				canopyOutput, Cluster.INITIAL_CLUSTERS_DIR + "-final"), output,
				convergenceDelta, maxIterations, true, 0.0, false);
		// run ClusterDumper
		ClusterDumper clusterDumper = new ClusterDumper(new Path(output,
				"clusters-*-final"), new Path(output, "clusteredPoints"));
		// clusterDumper.printClusters(null);
	}
}
