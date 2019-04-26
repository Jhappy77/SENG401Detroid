# DETROID [![Build Status](https://travis-ci.org/ViktorC/DETROID.svg?branch=master)](https://travis-ci.org/ViktorC/DETROID) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=net.viktorc:detroid&metric=ncloc)](https://sonarcloud.io/dashboard?id=net.viktorc:detroid) [![Quality Gate](https://sonarqube.com/api/project_badges/measure?project=net.viktorc:detroid&metric=coverage)](https://sonarcloud.io/dashboard?id=net.viktorc:detroid) [![Docs](https://img.shields.io/badge/docs-latest-blue.svg)](http://viktorc.github.io/DETROID)

## Engine
The **DETROID** engine is a chess engine built onto the **DETROID** framework. It uses a magic bitboard-based pos representation and [Zobrist](https://en.wikipedia.org/wiki/Zobrist_hashing) hashing. Furthermore, it employs a parallelized PVS algorithm with quiescence search within an iterative deepening framework with aspiration windows. It also utilizes a transposition table and an evaluation hash table. It facilitates search selectivity via adaptive null-move pruning, adaptive late move reductions, futility pruning, razoring, and fractional search extensions for checks, pushed pawns, single-replies, and recaptures. It relies on IID, staged move generation, MVV-LVA, SEE, killer heuristics, and relative history heuristics for move ordering. The engine also performs search time reductions and extensions based on factors such as the score associated with the current PV or the remaining time after the completion of a ply. The evaluation function relies on piece-square tables; the number of pinned pieces weighted by their type; pawn-piece defense and attack; piece mobility; bishop pair advantage; the pawn structure; king-pawn tropism determined by the average Manhattan distance of the king to enemy and friendly pawns weighted by whether they are passed, backward, or normal pawns; queen-king tropism determined by the average Chebyshev distance; tapered evaluation; and tempo advantage. All the chess heuristics employed by the engine are based on articles on [chessprogramming.org](https://www.chessprogramming.org). The engine also supports [Polyglot](http://wbec-ridderkerk.nl/html/details1/PolyGlot.html) chess opening books and, on Windows and Linux (both 32 and 64 bit) platforms, [Gaviota](https://sites.google.com/site/gaviotachessengine/) endgame tablebases. The Gaviota tablebases are probed using a shared [library](https://github.com/ViktorC/GaviotaTB-for-DETROID) exposing the original C probing code via JNI.

The engine offers the following UCI options:
* **Hash [spin]**: The hash size allocated for the transposition and evaluation tables in MB.
* **ClearHash [button]**: Clears the hash.
* **Ponder [check]**: Whether pondering is allowed by the engine.
* **OwnBook [check]**: Whether the engine should use its opening book.
* **PolyglotBookPrimaryPath [string]**: The path to the primary Polyglot opening book. Accepts both absolute and relative (to the engine's executable) paths.
* **PolyglotBookSecondaryPath [string]**: The path to the secondary Polyglot opening book; if the pos is not found in the primary book, the engine will look for it in the secondary book. Accepts both absolute and relative paths.
* **GaviotaTbLibPath [string]**: The path to the Gaviota probing library. Accepts both absolute and relative paths.
* **GaviotaTbPath [string]**: The paths to the folders containing the actual tablebase files. The paths can be delimited by semi-colons. Accepts both absolute and relative paths.
* **GaviotaTbCompScheme [combo]**: The compression scheme used by the specified tablebases.
* **GaviotaTbCache [spin]**: The size of the cache the probing library should use in MB.
* **GaviotaTbClearCache [button]**: Clears the probing cache.
* **SearchThreads [spin]**: The number of threads to use for searching.
* **ParametersPath [string]**: The path to the XML file containing the values for all parameters. Accepts both absolute and relative paths. The default path is _params.xml_ which has the engine use its internal parameters file unless there is such a file in the folder containing the engine's jar. If there is, it will be preferred over the internal parameters file; this allows for easy experimentation with different parameter values and for their optimization without the need to recompile the engine.
* **UCI_Opponent [string]**: The name of the opponent.
* **UCI_AnalyseMode [check]**: Whether the engine should run in analysis mode. In analysis mode, even single-reply positions are searched and no books or table-bases are used.

An important feature of the engine is its parallel search implementation. It is based on [Peter Österlund](http://www.talkchess.com/forum/viewtopic.php?t=64824&start=43) and [Tom Kerrigan](http://www.tckerrigan.com/Chess/Parallel_Search/)'s descriptions of algorithms combining ABDADA and Lazy SMP. Its main advantages are its relatively good speedup scaling, its lack of negative impact on single-thread performance, and its mitigation of the effects of erroneous pruning (thanks to the widened search tree). The table below illustrates how the search algorithm's performance scales with the increasing number of search threads on a quad-core processor. The second column displays the average relative search speed measured in *nodes per second* (NPS) while the third column shows the relative *time to depth* (TTD). The test has been performed on a set of 128 positions searched to an average fixed depth of 10.82 plies.

| Threads | NPS | TTD |
| :---: | :---: | :---: |
| 1 | 1 | 1 |
| 2 | 1.93 | 0.60 |
| 3 | 2.80 | 0.48 |
| 4 | 3.44 | 0.44 |

The engine relies almost exclusively on tunable parameters for its search and evaluation. Its 550+ static evaluation parameters have been optimized for 2 days using an unfiltered FEN-file of about 6,000,000 positions from roughly 50,000 games of self-play. Its search and engine control parameters have been tuned in two turns. First all the search control and time management parameters, which made up 158 bits, had been optimized for 4 weeks on an 8-core [Google Compute Engine](https://cloud.google.com/compute) instance, then the hash size distribution and hash entry lifecycle parameters, another 15 bits, were tuned for another 5 days with longer time controls. (**UPDATE:** The engine has gone through significant changes since then which rendered the values of most of these parameters greatly suboptimal. They need to be retuned for optimal playing performance.)

## Framework
The **DETROID** framework is a chess engine framework that implements the UCI protocol, provides a dynamic GUI, supports machine learning based parameter optimization, and offers some low-level utilities for the development of performant chess engines. Engines need to implement the `UCIEngine` interface to be usable as UCI chess engines and the search engines of the GUI. To be optimizable, they have to implement the `TunableEngine` interface which is an extension of `UCIEngine`. The interface between the framework and the engines could be described as a simplified Java translation of the UCI protocol that uses strings and primitives for data exchange whenever conveniently possible and relies on the observer pattern to handle asynchrony. This allows for flexibility and a high level of freedom to implement the actual chess engine without any restrictions on the data structures and algorithms to use while the framework deals with the secondary aspects of chess engine development. It also makes the wrapping of UCI compatible engines into an implementation of this interface fairly straightforward. The framework includes the `Detroid` chess engine which is used as the controller engine of the GUI and the tuning processes by default (ensuring the legality of moves and keeping track of game information). This makes it possible to plug engines into the framework without the need to implement the more complex `ControllerEngine` interface. Additionally, the framework also provides a number of utility classes such as `Cache`, a fast, generic pre-allocated lossy cuckoo hash table implementation, `SizeEstimator`, a utility for accurately computing the memory size of entire object graphs on the HotSpot JVM's heap, and `BitOperations` for bit-twiddling. The complete Javadoc of the framework can be found [here](http://viktorc.github.io/DETROID/).

The entry point of the framework is the `EngineFramework` class which is a `Runnable` implementation with a single two-parameter constructor. One of the parameters is an array of string arguments which define the operation mode and behaviour of the framework. The other parameter is an instance of the `EngineFactory` interface which is responsible for creating instances of `UCIEngine`, `TunableEngine`, and `ControllerEngine`. This is where chess engines can be plugged into the framework and where `Detroid` is specified as the default controller engine. In most application modes, including UCI and GUI mode, the only non-default method of the factory interface, `newEngineInstance`, is used to create the (non-controller) chess engine(s), thus, for these modes, it suffices to implement only the `UCIEngine` interface; however, for the tuning and conversion modes, the method used is `newTunableEngineInstance` which is expected to return a `TunableEngine` instance (by default, it simply type casts the instance returned by `newEngineInstance`). Given these parameters, the framework can be set up and launched effortlessly. To do so, an instance of `EngineFramework` needs to be constructed and simply run in the main method of the application. The main method's arguments should be forwarded to the `EngineFramework` instance's constructor so that the framework's mode of operation can be specified via program arguments once the application is compiled into an executable. The following sections describe these operation modes and features including the usage of the program arguments necessary to enable them.

### GUI
If the framework is run without program arguments, it defaults to a JavaFX GUI mode. The GUI uses a `ControllerEngine` instance to keep the game state and check proposed moves for legality, and it uses a `UCIEngine` instance to search the positions and propose moves. Both instances are created by the provided `EngineFactory`.

![gui0](https://user-images.githubusercontent.com/12938964/37392756-593ea498-276f-11e8-821a-b9ff69bfc917.jpg)

The GUI offers several useful functionalities for the testing and debugging of the engine used for searching. It has a table for real time search statistics, a chart for the search result scores returned over the course of the game, a debug console, a dialog for the UCI options provided, and a demo mode which has the engine play against itself. Furthermore, it supports pondering, time control settings, [FEN](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) and [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) parsing and extraction, changing sides, and undoing moves.

![gui1](https://user-images.githubusercontent.com/12938964/37392757-5958c9f4-276f-11e8-8de1-72b7a5a41b6d.png)

The UCI option dialog is generated dynamically based on the UCI options provided by the search engine. Different option types are represented by different GUI controls; e.g. spin options are numeric text fields, combo options are dropdown menus, check options are check boxes, etc. Backed by these features, the GUI's primary aim is to assisst the interactive testing of chess engines as there usually are a number of kinks and bugs that cannot easily be discovered any other way.

### UCI
When run in UCI mode, the framework handles the [Universal Chess Interface](http://wbec-ridderkerk.nl/html/UCIProtocol.html) protocol for the `UCIEngine` instance created by the provided `EngineFactory`. This allows the search engine to function as a UCI compliant chess program, as required by several chess GUIs and other tools.  
**Usage:** `-u`

### Tuning
Perhaps the most important feature of the framework is its parameter tuning support. Chess engines using this functionality of the framework are expected to implement the `TunableEngine` interface. This interface requires them to use a subclass of `EngineParameters` to define the parameters to tune by annotating the corresponding member variables of the class with the `Parameter` annotation. Only primitives are allowed to be marked as parameters. The parameters are not allowed to take on negative values, thus the most significant bit of all signed integers and floating point types are ignored. The `Parameter` annotation takes two optional arguments, the `ParameterType` and a byte value, `binaryLengthLimit`, that limits the number of bits considered when tuning. The type is used to specify whether a parameter is a static evaluation parameter, a search control parameter, or an engine management parameter; the significance of this will be explained in the following paragraphs. The default type is static evaluation. The `binaryLengthLimit` can be used to restrict the number of values to consider when tuning, if the maximum value the parameter can or should take on is known and it is smaller than the maximum value of its primitive type. This can speed up the evolutionary algorithm based tuning process (but has no effect on the performance of the gradient descent based one).

#### Optimization
Two different parameter optimization methods are supported by the framework. The first one is a [Population-based Incremental Learning](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.61.8554) algorithm with a self-play based fitness function inspired by Thomas Petzke's [work](http://macechess.blogspot.co.at/2013/03/population-based-incremental-learning.html) on his chess engine [ICE](http://www.fam-petzke.de/cp_ice_en.shtml). It can be used to tune static evaluation parameters, search control parameters, engine management parameters, different combinations of these, or all. Its mandatory parameters are the population size, the number of games the engine should play against itself, and the time control for the games in milliseconds. The optional parameters are the type of parameters to tune (`eval`, `control`, `management`, `eval+control`, `control+management`, or `all`) which defaults to `all`; the time increment per move in milliseconds, 0 by default; the validation factor which determines the factor of the original number of games played to play in addition in case a parameter set is found to be the fittest of its generation, by default 0; a flag, by default false, denoting whether the `OwnBook` parameter of the engine, if it exists, should be set to true; the number of MBs the hash size of the engine should be set to if it supports the corresponding UCI option; the initial probability vector which can be set to continue the tuning process from a certain generation by taking the probability vector logged for it; the log file path, by default _log.txt_; and the number of processors to use, by default 1. High levels of concurrency can be detrimental to the quality of the optimization results; it is not recommended to use a value higher than the number of available physical cores divided by two.  
**Usage:** `-t selfplay -population 100 -games 100 -tc 2000 --paramtype control --inc 10 --validfactor 0.5 --trybook true --tryhash 8 --initprobvector "0.9, 0.121, 0.4" --log my_log.txt --concurrency 2`

The other optimization method uses a stochastic gradient descent algorithm with [Nesterov-accelerated Adaptive Moment Estimation](http://cs229.stanford.edu/proj2015/054_report.pdf) to minimize the [Texel](https://www.chessprogramming.org/Texel%27s_Tuning_Method) cost function. As opposed to the original Texel method, it uses static evaluation instead of quiescence search for the sake of efficiency. It also allows for the definition of the symbolic gradient of the evaluation function; if that is not provided, it approximates the gradient using numerical differentiation. It can only be applied to static evaluation parameter optimization, but it is a lot more efficient at that than the evolutionary algorithm based method. However, this requires a so called FEN-file which contains labelled data entries consisting of a pos in FEN and the result of the game the pos occurred in. This tuning method's only mandatory parameter is the batch size which determines the number of data entries to use per batch. The optional parameters are `costbatchsize`, the number of samples to include in a batch when calculating the total training and test costs, by default 2 million; `k`, a constant used in the cost function calibrated to achieve the lowest costs, if it is not set, it is calibrated before the tuning begins (on the entire training data set); the number of epochs the optimization should span, by default 0 which means it goes on infinitely; `h`, the step size to use for numerical differentiation, by default 1; the base learning rate which determines the initial step size of the gradient descent and by default is 1; the annealing rate by which the learning rate is multiplied after every epoch, by default 0.99; the proportion of the entire data set that should be used for testing, by default one fifth; the path to the FEN-file, by default _fens.txt_; the log file path, by default _log.txt_; and the number of processors to use, by defualt 1. In the case of this optimization method, parallelism cannot have an effect on the quality of the results, thus it is recommended to use the number of available physical cores as the concurrency argument.  
**Usage:** `-t texel -batchsize 16000 --costbatchsize 1000000 --k 0.54 --epochs 100 --h 2.0 --learningrate 1.5 --annealingrate 0.8 --testdataprop 0.3 --fensfile my_fens.txt --log my_log.txt --concurrency 4`

#### FEN-file generation
The framework provides two different ways to generate a FEN-file for static evaluation tuning. The first way is self-play. With the exception of one, all parameters and their descriptions can be found in the paragraph describing the self-play based optimization method. The only novel parameter is the target path for the FEN-file which defaults to _fens.txt_. Positions arising from book moves or positions in which the engine found a mate are not logged to the FEN-file. For short time controls (below 2s), concurrency is not recommended to have a value greater than the number of available physical cores divided by two.  
**Usage:** `-g byselfplay -games 60000 -tc 2000 --inc 10 --trybook true --tryhash 8 --destfile my_fens.txt --concurrency 2`

The other way is PGN conversion. This requires a PGN file of chess games which can then be converted to a FEN-file. The only mandatory parameter is the file path to the PGN file. The optional parameters are the maximum number of games from the PGN file to convert, and the file path of the generated FEN-file.  
**Usage:** `-g bypgnconversion -sourcefile my_pgn.pgn --maxgames 50000 --destfile my_fens.txt`

#### FEN-file filtering
The generated FEN-files can also be filtered to possibly improve the optimization results. For example, all the positions and their labels from drawn games can be removed from the FEN-file. The file path to the source FEN-file is a mandatory parameter, while the destination file path is optional and defaults to `fens.txt`.  
**Usage:** `-f draws -sourcefile old_fen.txt --destfile new_fen.txt`

The same can be done for obvious mates as well (mates found by the 0-depth quiescence search of the engine to be tuned). This is only needed, and in fact, highly recommended, if the FEN-file is generated from a PGN file.  
**Usage:** `-f mates -sourcefile old_fen.txt --destfile new_fen.txt`

Tactical positions can also be removed from FEN-files. This can be used to make sure that only quiet positions are left and thus the candidate engine's static evaluation function can better assess the position's score. If the original data set is big enough, it is highly recommended to keep only the quiet positions.  
**Usage:** `-f tacticals -sourcefile old_fen.txt --destfile new_fen.txt`

Another way to filter FEN-files is removing the first `x` positions from each game. The only new parameter defines the value of `x`; the others are the same as above.  
**Usage:** `-f openings -sourcefile old_fen.txt -firstxmoves 8 --destfile new_fen.txt`

#### Parameter conversion
Last but not least, the outputs of the two optimization methods logged in their log files can be converted into XML files containing the optimized values of the parameters. The PBIL algorithm logs the probability vector of each generation. This can be converted into an XML file by specifying the value argument using the probability vector from the log file. The other two optional parameters are the type of the parameters to convert which defaults to `all` and the destination path for the XML file which defaults to _params.xml_. The type should be the same as what was used for optimization. It should also be noted that if the engine's relevant parameters are changed (name, type, or binary length limit) after the completion of the optimization process, the logged values cannot reliably be converted anymore using the engine with the changed parameters.  
**Usage:** `-c probvector -value "0.9, 0.121, 0.4" --paramtype control --paramsfile my_params.xml`

The static evaluation parameter optimization method using the Texel cost function logs the values of the optimized parameters as an array of decimals called `parameters`. It can be converted to an XML file almost exactly as described above, using the parameters to specify the `value` argument and omitting `paramtype`.
**Usage:** `-c parameters -value "124.12357, 5.02345, 2.98875" --paramsfile my_params.xml`
