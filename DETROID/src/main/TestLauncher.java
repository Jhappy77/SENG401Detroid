package main;

import engine.Detroid;

public class TestLauncher {

	public static void main(String[] args) {
		Detroid d = new Detroid();
		d.init();
		d.getParameters().set(new double[] { 20000.0, 1321.2384672841567, 612.9450599347257, 338.7795663061516, 313.8304219747643, 90.21820406618222, 0.0, 4.0, 2.0, 1.0, 1.0, 0.0, 21.43634920348089, 7.8180154623341815, 23.982108297771532, 56.34406685453956, 7.448380904040526, 6.163121953984208, 3.2935827016698047, 0.7766221319745208, 21.745161216266713, 0.0, 3.4796184240213868, 6.9619793536793875, 7.191750449734262, 7.770242550743663, 31.45135816061133, 44.16464070006128, 46.177836583337395, 86.16264072903576, 0.9662362397458437, 0.3414280000067722, 2.218684526630716, 4.2174027291625125, 59.45048954631014, 60.10858772099859, 61.24049966849491, 37.202409910693625, 35.63031572104679, 51.29201837976263, 1.9697669518575494, 160.0, 0.0, 22.0, 171.0, 256.0, 2.0, 1.0, 4.0, 506.0, 700.0, 900.0, 100.0, 270.0, 8.0, 4.0, 6.0, 8.0, 6.0, 60.0, 12.0, 16.0, 15.0, 1.0, 1.0, 1.0, 3.0, 439.0, 43.0, 5.0, 40.0, 33.0, 85.0, 127.0, 127.0, 126.90829289786467, 127.0, 126.36204267058812, 125.92159877089827, 126.99064994071719, 126.34478065658142, 34.688555972719826, 75.1136599573347, 2.9830972840872914, 0.0, 94.24353322117734, 19.31948064975274, 53.9096511278318, 58.982296399228495, 24.496296682484985, 8.49862804716973, 43.79293088737997, 6.764027771354786, 9.913655566286412, 4.738155066204865, 14.494108302821882, 58.72617442185577, 14.58024025453907, 9.669692241955312, 16.807141222832026, 13.486322995343732, 57.784314277154046, 5.028846868053815, 0.8670797092672468, 0.0, 0.21911146237921347, 8.607946333782493, 0.3626517898190741, 38.13073359415201, 18.828930065640453, 38.44125881975911, 5.2907553283748765, 26.74155849481659, 0.0, 0.5167229596982513, 0.26551329086331343, 0.02270065769409373, 62.06741110248301, 42.221418765501845, 42.847321617741954, 1.2514770053923319, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 127.0, 116.52110740674367, 127.0, 117.25828398540386, 126.92195660401762, 108.26596150944219, 72.00616228868309, 100.5350223024932, 54.00567526473584, 58.457603175419635, 55.05348182217474, 59.88168758342303, 55.64104532867021, 32.23102569256549, 44.71366537503837, 44.556712554464234, 44.968160672438685, 22.033230226721166, 34.808863281934265, 27.76361125522144, 42.73079267413922, 21.12139330221893, 25.298588318042835, 43.14206830321872, 29.370019580379555, 44.589633818336104, 34.49383494709372, 24.683405599398323, 30.672633882286913, 6.157458014318023, 27.043933204439956, 54.04193499417064, 30.042621711920653, 42.214188140495416, 0.0, 17.026959935674977, 0.04039370902327147, 0.0, 0.13156318219993995, 59.899812295287965, 4.10159133747623, 52.21899689844122, 52.81919646075306, 10.611628692317423, 56.73559294867318, 29.144756745525516, 125.0991952224176, 10.619677686391366, 95.45716231969651, 126.45546567613745, 93.81947855535215, 41.35372981190454, 109.2016946699294, 43.82878166155379, 90.05253803460663, 91.22222961754652, 48.867246641691274, 38.96796177482745, 52.65380315596594, 60.91143849995029, 28.167787159890338, 83.08736549882424, 93.90360573325951, 122.86915596845749, 63.3327372305963, 74.98854339526547, 80.92503991862232, 80.64153836535493, 34.29158819915617, 77.29108637672022, 14.969789210478721, 107.29469194504007, 70.8986903476295, 36.17428486902456, 41.96424512030679, 50.6512151617508, 78.87500115841308, 82.18659856006911, 45.237235196265466, 104.30227999792636, 64.31295474030775, 76.068369737128, 31.83784142192854, 79.69588539975801, 58.48432971762573, 96.91835455437689, 37.98031791942632, 58.69150477292905, 76.0232280632388, 77.39057471214464, 27.578176697849674, 70.7866784585592, 76.82483617339464, 53.14832199627224, 6.931117915607406, 61.63304240535739, 57.9069728761664, 51.79010434385341, 1.5001469351764452, 27.621518931076082, 36.87258727559361, 38.74560235750892, 6.256734994935538, 63.84247718239069, 70.82567215607294, 20.41183074397926, 44.407797602263614, 35.65832160496768, 27.122389073258386, 39.38737960481298, 84.52505961847893, 48.535793444508435, 54.85751558777789, 31.015786123587826, 12.989566342564215, 31.90423151464376, 40.104293855927395, 58.02504750103805, 47.43861844385738, 21.241105967363413, 38.02931227295475, 53.23760506254587, 46.9705944181809, 53.88927656452791, 52.83804812169888, 24.979298065500686, 9.68091286771713, 41.40458136757115, 54.16841689827231, 51.39175815430911, 1.5452393269430655, 19.526194166208168, 48.606886826451486, 52.990487548721916, 32.84574251745387, 39.6293417495301, 86.31722511204546, 63.14443124237974, 33.48583109432298, 10.46767834743846, 98.64345240864408, 29.7786555904683, 61.22799182965778, 65.18350895889641, 101.37360203098262, 124.01177067756427, 126.36694673053731, 126.23335080664856, 116.26004192832328, 116.07455404060659, 86.28825025889543, 112.83290599873779, 25.215047735553043, 37.037775410423635, 87.41503842046282, 71.74844115272107, 32.2824297206163, 75.82507676972051, 23.607761666965747, 10.652807251975448, 66.85328319440939, 68.35709281988073, 51.34580955428221, 23.53082952535019, 7.883589659346262, 66.96921473100988, 68.39883318529506, 29.843820853524065, 0.3989198632344527, 13.27427849060054, 41.87114921972744, 37.68415062139522, 21.160238968567118, 51.26358251058734, 26.279748196597247, 37.88677621531357, 45.903722511374134, 26.759890658220808, 32.48873519929012, 72.89148366640785, 69.89244612006667, 15.91906236673181, 45.48281947620536, 75.84091881811601, 21.49771905829197, 36.78543183859129, 0.6214860999646763, 54.66496595450443, 6.335444790819629, 0.0, 56.395029755766295, 95.83299152630664, 73.19735027969797, 103.74955914353905, 126.03134166273401, 110.76033063749423, 68.29782925256569, 0.0, 116.29478728646124, 92.45375804055172, 103.33806062505793, 102.14083379157938, 93.11315538569289, 99.1838734976055, 96.987496694323, 126.80538194208727, 89.62004225532941, 103.08329415242099, 93.80052939534168, 87.9916742967031, 104.99212444547358, 75.89177803313976, 97.22524578434098, 88.82162144035179, 94.9051683927422, 83.57817428381114, 113.51813610853777, 95.78412848470397, 97.52789666516318, 75.14796618902419, 92.2486738728693, 88.93578876688686, 82.78152033603946, 98.62977103274247, 84.91865631207051, 83.12083884039848, 90.37611999080731, 92.70022271180659, 78.29065313817252, 77.42033306162772, 63.714280726221304, 91.35245962115526, 91.92439549556629, 104.14183988738309, 79.31791890426017, 85.18123420092229, 62.16177367048035, 53.62331302452306, 64.92431307774629, 55.27600790337979, 60.098643719323476, 71.97452246795781, 58.85390218644028, 38.17277886746397, 41.99525071707041, 41.37961882592908, 65.81739031521002, 74.73113658025964, 79.37102052874035, 88.4643684357599, 88.53192374388038, 62.47313723367117, 58.71035767655088, 41.48007158057826, 22.842996823637, 41.4788544406585, 61.95072372154187, 69.135645854997, 60.74204662800787, 59.086946172303975, 57.44923337178565, 16.895209029720043, 6.984024484444725, 63.331479611800205, 74.76350484144967, 77.07613963552909, 49.32051714236978, 75.0918224544813, 126.53117513302345, 66.93302056235132, 2.7997826661480474, 29.64474868923336, 59.516100818246564, 87.36291686077588, 73.68870055063346, 50.744025486882556, 44.80777816633821, 127.0, 33.83423350028273, 3.449810269862764, 47.657852784381554, 75.50722874558375, 90.00303353014182, 120.10670753392004, 92.00922771593476, 127.0, 0.0, 37.65743829439401, 39.16882221887604, 37.71195768324371, 66.82554669778878, 89.92928544981021, 92.56152489345158, 72.56107001684128, 6.7996103061468975, 14.90522001993703, 38.16249619382842, 47.65837520657733, 86.3892989617725, 47.24237195411359, 76.92906257364068, 68.51751698628226, 14.54559783748337, 14.009822165618202, 33.6150046498522, 53.557032535684414, 45.17677901456933, 70.72464538174434, 87.08136664033822, 63.54895105641677, 73.58512168225761, 9.378648750246036, 28.593667728632187, 59.220674675055534, 44.35087456388208, 59.68059802340993, 29.260413584198957, 27.050539713469714, 0.0, 21.74996466180879, 63.46586038305693, 86.38560773777621, 98.24911605890733, 73.03844278471972, 0.0, 17.156652260432125, 0.0, 86.7136493509602, 39.941348344856216, 16.32298564483295, 124.0777626307674, 52.29334133324612, 67.63709884616834, 98.2047613526803, 126.62983935563867, 36.063385949110305, 94.15671885466641, 66.7538059966659, 116.712845603228, 126.65893699251126, 127.0, 65.1274926442475, 22.138093581522284, 53.02225736796216, 1.1232092058545504, 0.26796700356254455, 0.2758199446316023, 66.8872907253395, 126.9370805373822, 9.896212272125963, 102.81301709282894, 116.03509728959958, 80.00482482896055, 1.3753126705852547, 3.7906069713070405, 0.0, 57.35053660098025, 1.161124523960079, 111.10949419981054, 100.27075839635152, 35.00444952362827, 79.81636400416538, 33.65772297170322, 79.5096348442755, 62.01291849121764, 0.8291372804667899, 123.45230960869546, 123.35093431627695, 118.42235119935286, 92.80519622826932, 77.9177216876418, 51.74605843124639, 52.34153336851932, 59.500437185179386, 79.41120136998069, 127.0, 124.74246576766717, 49.27337261391411, 32.18885194953624, 16.861514167315775, 8.793425117763968, 55.82480554506801, 42.225975982731, 113.50793728436348, 103.19451573937633, 0.0, 84.76938867043057, 19.191176701216158, 24.943745183176592, 29.397124056653137, 0.0, 32.59234178510177, 6.932106361765481, 60.8945948530111, 126.75474234056804, 105.1717059817356, 116.80779372665147, 4.652956189167221, 95.35526515191205, 126.8643190430351, 126.21718702907401, 127.0, 124.87570388239192, 126.95413368171492, 127.0, 125.83868467581858, 105.41794513855659, 123.6936355513726, 124.49726329108822, 125.01066523032407, 107.55106476474447, 123.8758822933439, 127.0, 99.17418715370249, 50.489529602927455, 79.89483633280638, 68.27305381455156, 62.11922929238452, 85.32609960061095, 105.94583004279394, 97.03419470177796, 49.83062686830054, 21.176681827746112, 38.10370275908156, 49.59771966458629, 49.67864162060245, 69.68252826271335, 71.1592204798274, 59.82822066279042, 41.29978425548663, 27.957881490625034, 9.680156622175627, 32.395281615422036, 46.75345461971786, 61.44615103228724, 72.05648128129457, 44.11500139620993, 30.77423722086436, 14.304095559560073, 39.40501073612004, 17.27202423750691, 55.87429130472512, 56.64032659194417, 70.75547778394903, 49.341967495201075, 25.35545921635673, 81.01795692078272, 3.418404049138622, 0.6116338023257201, 20.1841354120228, 4.23562875144211, 5.432840903098771, 0.6139920449173133, 0.0 });
		System.out.println(d.getParameters());
	}

}