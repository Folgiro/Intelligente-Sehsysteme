import numpy as np


def get_co_occ_mat(image, alpha):
    co_occ_mat = np.zeros((4, 4))  # Co-Ocurence -Matrix
    sin_alpha = int(np.sin(alpha))
    cos_alpha = int(np.cos(alpha))
    # berechne Co-occurence-Matrix p
    for i_image in range(0, 4):
        for j_image in range(0, 4):
            sum = 0
            # get summe der Produkte der dirac-Masse. 1. Spalte bei uns immer 0
            for i_region in range(1, 4):
                for j_region in range(0, 4):
                    if (image[i_region][j_region] - image[i_image][j_image] == 0
                            and image[i_region - sin_alpha][j_region - cos_alpha] == 0):
                        sum = sum + 1
            co_occ_mat[i_image][j_image] = 1 / 16 * sum
    # normiere co_occ_mat
    norm_faktor = np.array(co_occ_mat).sum()
    co_occ_mat = [1 / norm_faktor * pixel for pixel in co_occ_mat]
    return co_occ_mat


def get_energie(matrix):
    for i in range(0,4):
        for j in range(0,4):
            matrix[i][j]=matrix[i][j]*matrix[i][j]
    return np.array(matrix).sum()


def get_kontrast(matrix):
    for i in range(0,4):
        for j in range(0,4):
            matrix[i][j]=(i-j)*(i-j)*matrix[i][j]
    return np.array(matrix).sum()


def get_entropie(matrix):
    for i in range(0,4):
        for j in range(0,4):
            matrix[i][j]=matrix[i][j]*np.log2(matrix[i][j])
    return -np.array(matrix).sum()


def get_homogenitaet(matrix):
    for i in range(0,4):
        for j in range(0,4):
            matrix[i][j]=matrix[i][j]/(1+np.abs(i-j))
    return np.array(matrix).sum()


def get_texturmasse(image, alpha):
    file_dir = "//home/akoerner/Sonstiges/Studium/ISS/ISS_WS2122_Aufgabenblatt05/aufgabe4_res.txt"
    energie = get_energie(get_co_occ_mat(image, alpha))
    kontrast = get_kontrast(get_co_occ_mat(image, alpha))
    entropie = get_entropie(get_co_occ_mat(image, alpha))
    homog = get_homogenitaet(get_co_occ_mat(image, alpha))


    f = open(file_dir, "a")
    f.write(f'alpha: {alpha}\\\\\n'
            f'delta: 1\\\\\n'
            f'\\[Region: {to_latex_matrix(image)} \\\\ \n'
            f'\\qquad \\qquad\n'
            f'P_{alpha},1 = {to_latex_matrix(get_co_occ_mat(image, alpha))} \\]\\\\\n'
            f'energie: {energie} \\\\\n'
            f'kontrast: {kontrast}\\\\\n'
            f'entropie: {entropie}\\\\\n'
            f'homogenitaet: {homog}\\\\\n'
            f'\n'
            )
    f.close()


def to_latex_matrix(matrix):
    tex_mat = '\\begin{pmatrix} \n'
    for i in range(0,4):
        for j in range(0,4):
            tex_mat = tex_mat + f'{matrix[i][j]} '
            if j==3:
                tex_mat = tex_mat + f' \\\\'
            else:
                tex_mat = tex_mat + f' & '
    tex_mat = tex_mat + '\\end{pmatrix}'
    return tex_mat

if __name__ == '__main__':
    image1 = [[0,0,0,0], [0,0,0,0], [3,3,3,3], [3,3,3,3]] # eingabebild
    image2 = [[3, 3, 3, 3], [3, 0, 0, 3], [3, 0, 0, 3], [3, 3, 3, 3]]  # eingabebild
    image3 = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]]  # eingabebild
    alpha = np.pi/2
    #alpha = 0
    for image in [image1, image2, image3]:
        get_texturmasse(image, alpha)





