# NodeFetcher
Java tool to read extract certain nodes from an ABAQUS input file, link it with the corresponding outputs and export it to EXCEL

The latest version can be downloaded from the [Releases](https://github.com/smrg-uob/NodeFetcher/releases)

Can be used from Command Line or from the GUI
Double clicking the .jar executable will launch the gui.

## User Interface
![User Interface](https://github.com/smrg-uob/NodeFetcher/blob/master/doc/gui.png)

To use the interface, follow the following steps:
1. Use the first "Browse" button on the right to select an ABAQUS input (.inp) file
1. Use the second "Browse" button on the right to select an ABAQUS report (.rpt) file. The "None" button can be used to not specify a report file, in which case only the node coordinates will be exported. To create an ABAQUS report file, in abaqus:
   1. On the menu bar under "Report" choose "Field Output"
   1. On the "Variable" tab choose "Unique Nodal" and then check the output parameters to be included
   1. On the "Setup" tab choose a filename and make sure "Annotated Format" is checked, but "Separate Table..." is unchecked
   1. Below, "Field outupt" should be checked, but "Column totals" and "Column min/max" unchecked. It should look like this: ![Field Output Report](https://github.com/smrg-uob/NodeFetcher/blob/master/doc/abaqus_report.png)
1. On the dropdown, specify which set of nodes (as assigned in the input file) to export, or click "None" to export all nodes
1. Select an EXCEL file (.xls or .xlsx) to export the data to by clicking the last "Browse" button, or specify a new filename by clicking the "New" button to export to a new EXCEL file. The data will appear on a new sheet with the same name as the chosen node group. Existing Excel files will never be overwritten, however, if a sheet with the same name exists already, the data on that sheet will be overwritten.

When an input has been chosen, a check mark will appear on the right. When all boxes are checked, the "Run" button will light up. Click "Run" to export the chosen data to the chosen EXCEL file, 
The "Clear" button can be clicked at any time to reset all the chosen inputs.
After exporting one node group, another node group can be selected and exported without having to redefine all inputs. The data of this new node group will appear in another Excel sheet with the name of the node group.

## Command Line
The advantage of using the command line is that verbose feedback (such as logging info or stacktraces) will be printed to the terminal in case something goes wrong.
To use the command line:
1. Use the command line and `cd` to the directory containing the jar file.
1. Run `java -jar NodeFetcher.jar input=<inputfile.inp> data=<reportfile.rpt> filter=<node_group> out=<output.xlsx>`. In this
   1. `<inputfile.inp>` is the path (or filename if it is in the same directory) to the ABAQUS input file
   1. `<reportfile.rpt>`is the path (or filename if it is in the same directory) to the ABAQUS report file
   1. `<node_group>` is the name of the node set as specified in the input file to export
   1. `<output.xlsx>` is the path (or filename if it is in the same directory) to the EXCEL output file, if the file does not yet exist, a new one will be created
   
An example command could be:
`java -jar NodeFetcher.jar input=input.inp filter=Face_1 data=output.rpt out=results.xlsx`


   
