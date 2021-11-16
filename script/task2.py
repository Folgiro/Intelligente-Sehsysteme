import numpy as np


def transform_array(a):
    labels_x = list(range(len(a[0])))
    labels_y = list(range(len(a) + 1))
    result = [[[[labels_y[0]], [-1]]] + [[[i], [-1]] for i in labels_x]]
    for i in range(0, len(a)):
        tmp = [[[labels_y[i + 1]], [-1]]]
        for j in range(len(a[0])):
            if isinstance(a[0][0], list):
                tmp += [[[a[i][j][0][0]], a[i][j][1]]]
            else:
                tmp += [[[a[i][j]], [-1]]]
        result.append(tmp)
    return result


def array_to_tex_table(a):
    a = transform_array(a)
    tex = "\\[\n\\begin{array}{"
    tex += "c|" + "".join("c" * (len(a[0]) - 1)) + "}\n"
    for i in range(len(a)):
        tex += "".join((f"{a[i][j][0][0]}" + (f" : {a[i][j][1]}" if a[i][j][1][0] != -1 else "") + " &" for j in
                        range(len(a[0]) - 1)))
        tex += f" {a[i][-1][0][0]}" + (f" : {a[i][-1][1]}" if a[i][-1][1][0] != -1 else "") + " \\\\" + (
            "\\hline \n" if i == 0 else "\n")
    tex += "\\end{array}\n\\]"
    return tex


def otsu(arr):
    """create first column result[0] = f result[1] = g"""
    result = [[0, 0, 1, 0, np.mean(arr), 0]]
    for T in range(1, max(arr)+1):
        nT = np.count_nonzero(arr == T)/len(arr)
        nB = result[T-1][1] + nT
        nO = result[T-1][2] - nT
        muB = (result[T-1][3] * result[T-1][1] + nT*T)/nB if nB != 0 else 0
        muO = (result[T-1][4] * result[T-1][2] - nT*T)/nO
        sigma2 = nB * nO * (muB - muO)**2
        result += [[T, nB, nO, muB, muO, sigma2]]
    return result


arr = np.array([3, 2, 4, 6, 7, 2, 2, 5, 3])
r = array_to_tex_table(np.array(otsu(arr)))
print(r)
