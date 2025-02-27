/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.stat.inference;

import java.util.Collection;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * A collection of static methods to create inference test instances or to
 * perform inference tests.
 *
 * @since 1.1
 * @version $Id: TestUtils.java 1422313 2012-12-15 18:53:41Z psteitz $
 */
public class TestUtils  {

    /** Singleton TTest instance. */
    private static final TTest T_TEST = new TTest();

    /** Singleton ChiSquareTest instance. */
    private static final ChiSquareTest CHI_SQUARE_TEST = new ChiSquareTest();

    /** Singleton OneWayAnova instance. */
    private static final OneWayAnova ONE_WAY_ANANOVA = new OneWayAnova();

    /** Singleton G-Test instance. */
    private static final GTest G_TEST = new GTest();

    /**
     * Prevent instantiation.
     */
    private TestUtils() {
        super();
    }

    // CHECKSTYLE: stop JavadocMethodCheck

    /**
     * @see TTest#homoscedasticT(double[], double[])
     */
    public static double homoscedasticT(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.homoscedasticT(sample1, sample2);
    }

    /**
     * @see TTest#homoscedasticT(org.apache.commons.math3.stat.descriptive.StatisticalSummary, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticT(final StatisticalSummary sampleStats1,
                                        final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.homoscedasticT(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#homoscedasticTTest(double[], double[], double)
     */
    public static boolean homoscedasticTTest(final double[] sample1, final double[] sample2,
                                             final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#homoscedasticTTest(double[], double[])
     */
    public static double homoscedasticTTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sample1, sample2);
    }

    /**
     * @see TTest#homoscedasticTTest(org.apache.commons.math3.stat.descriptive.StatisticalSummary, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticTTest(final StatisticalSummary sampleStats1,
                                            final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#pairedT(double[], double[])
     */
    public static double pairedT(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NoDataException,
        DimensionMismatchException, NumberIsTooSmallException {
        return T_TEST.pairedT(sample1, sample2);
    }

    /**
     * @see TTest#pairedTTest(double[], double[], double)
     */
    public static boolean pairedTTest(final double[] sample1, final double[] sample2,
                                      final double alpha)
        throws NullArgumentException, NoDataException, DimensionMismatchException,
        NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return T_TEST.pairedTTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#pairedTTest(double[], double[])
     */
    public static double pairedTTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NoDataException, DimensionMismatchException,
        NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.pairedTTest(sample1, sample2);
    }

    /**
     * @see TTest#t(double, double[])
     */
    public static double t(final double mu, final double[] observed)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(mu, observed);
    }

    /**
     * @see TTest#t(double, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double t(final double mu, final StatisticalSummary sampleStats)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(mu, sampleStats);
    }

    /**
     * @see TTest#t(double[], double[])
     */
    public static double t(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(sample1, sample2);
    }

    /**
     * @see TTest#t(org.apache.commons.math3.stat.descriptive.StatisticalSummary, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double t(final StatisticalSummary sampleStats1,
                           final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#tTest(double, double[], double)
     */
    public static boolean tTest(final double mu, final double[] sample, final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(mu, sample, alpha);
    }

    /**
     * @see TTest#tTest(double, double[])
     */
    public static double tTest(final double mu, final double[] sample)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(mu, sample);
    }

    /**
     * @see TTest#tTest(double, org.apache.commons.math3.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(final double mu, final StatisticalSummary sampleStats,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(mu, sampleStats, alpha);
    }

    /**
     * @see TTest#tTest(double, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(final double mu, final StatisticalSummary sampleStats)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(mu, sampleStats);
    }

    /**
     * @see TTest#tTest(double[], double[], double)
     */
    public static boolean tTest(final double[] sample1, final double[] sample2,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#tTest(double[], double[])
     */
    public static double tTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(sample1, sample2);
    }

    /**
     * @see TTest#tTest(org.apache.commons.math3.stat.descriptive.StatisticalSummary, org.apache.commons.math3.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(final StatisticalSummary sampleStats1,
                                final StatisticalSummary sampleStats2,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(sampleStats1, sampleStats2, alpha);
    }

    /**
     * @see TTest#tTest(org.apache.commons.math3.stat.descriptive.StatisticalSummary, org.apache.commons.math3.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(final StatisticalSummary sampleStats1,
                               final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquare(double[], long[])
     */
    public static double chiSquare(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException {
        return CHI_SQUARE_TEST.chiSquare(expected, observed);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquare(long[][])
     */
    public static double chiSquare(final long[][] counts)
        throws NullArgumentException, NotPositiveException,
        DimensionMismatchException {
        return CHI_SQUARE_TEST.chiSquare(counts);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTest(double[], long[], double)
     */
    public static boolean chiSquareTest(final double[] expected, final long[] observed,
                                        final double alpha)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed, alpha);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTest(double[], long[])
     */
    public static double chiSquareTest(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTest(long[][], double)
     */
    public static boolean chiSquareTest(final long[][] counts, final double alpha)
        throws NullArgumentException, DimensionMismatchException,
        NotPositiveException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(counts, alpha);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTest(long[][])
     */
    public static double chiSquareTest(final long[][] counts)
        throws NullArgumentException, DimensionMismatchException,
        NotPositiveException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(counts);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareDataSetsComparison(final long[] observed1,
                                                     final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return CHI_SQUARE_TEST.chiSquareDataSetsComparison(observed1, observed2);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareTestDataSetsComparison(final long[] observed1,
                                                         final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException,
        MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[], double)
     *
     * @since 1.2
     */
    public static boolean chiSquareTestDataSetsComparison(final long[] observed1,
                                                          final long[] observed2,
                                                          final double alpha)
        throws DimensionMismatchException, NotPositiveException,
        ZeroException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.OneWayAnova#anovaFValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaFValue(final Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException {
        return ONE_WAY_ANANOVA.anovaFValue(categoryData);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.OneWayAnova#anovaPValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaPValue(final Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException,
        ConvergenceException, MaxCountExceededException {
        return ONE_WAY_ANANOVA.anovaPValue(categoryData);
    }

    /**
     * @see org.apache.commons.math3.stat.inference.OneWayAnova#anovaTest(Collection,double)
     *
     * @since 1.2
     */
    public static boolean oneWayAnovaTest(final Collection<double[]> categoryData,
                                          final double alpha)
        throws NullArgumentException, DimensionMismatchException,
        OutOfRangeException, ConvergenceException, MaxCountExceededException {
        return ONE_WAY_ANANOVA.anovaTest(categoryData, alpha);
    }

     /**
     * @see GTest#g(double[], long[])
     * @since 3.1
     */
    public static double g(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException {
        return G_TEST.g(expected, observed);
    }

    /**
     * @see GTest#gTest( double[],  long[] )
     * @since 3.1
     */
    public static double gTest(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return G_TEST.gTest(expected, observed);
    }

    /**
     * @see GTest#gTestIntrinsic(double[], long[] )
     * @since 3.1
     */
    public static double gTestIntrinsic(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return G_TEST.gTestIntrinsic(expected, observed);
    }

     /**
     * @see GTest#gTest( double[],long[],double)
     * @since 3.1
     */
    public static boolean gTest(final double[] expected, final long[] observed,
                                final double alpha)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return G_TEST.gTest(expected, observed, alpha);
    }

    /**
     * @see GTest#gDataSetsComparison(long[], long[])
     * @since 3.1
     */
    public static double gDataSetsComparison(final long[] observed1,
                                                  final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return G_TEST.gDataSetsComparison(observed1, observed2);
    }

    /**
     * @see GTest#rootLogLikelihoodRatio(long, long, long, long)
     * @since 3.1
     */
    public static double rootLogLikelihoodRatio(final long k11, final long k12, final long k21, final long k22)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return G_TEST.rootLogLikelihoodRatio(k11, k12, k21, k22);
    }


    /**
     * @see GTest#gTestDataSetsComparison(long[], long[])
     * @since 3.1
     */
    public static double gTestDataSetsComparison(final long[] observed1,
                                                        final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException,
        MaxCountExceededException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @see GTest#gTestDataSetsComparison(long[],long[],double)
     * @since 3.1
     */
    public static boolean gTestDataSetsComparison(final long[] observed1,
                                                  final long[] observed2,
                                                  final double alpha)
        throws DimensionMismatchException, NotPositiveException,
        ZeroException, OutOfRangeException, MaxCountExceededException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2, alpha);
    }

    // CHECKSTYLE: resume JavadocMethodCheck

}
